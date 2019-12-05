package com.sybd.znld.light.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.light.config.ProjectConfig;
import com.sybd.znld.light.controller.dto.*;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.model.Pair;
import com.sybd.znld.model.StrategyStatus;
import com.sybd.znld.model.lamp.*;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.onenet.dto.BaseResult;
import com.sybd.znld.model.onenet.dto.CommandParams;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StrategyService implements IStrategyService {
    private final IUserService userService;
    private final StrategyMapper strategyMapper;
    private final StrategyTargetMapper strategyTargetMapper;
    private final StrategyPointMapper strategyPointMapper;
    private final LampMapper lampMapper;
    private final RegionMapper regionMapper;
    private final ElectricityDispositionBoxMapper electricityDispositionBoxMapper;
    private final ProjectConfig projectConfig;
    private final ObjectMapper objectMapper;
    private final IOneNetService oneNetService;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final OrganizationMapper organizationMapper;
    private final UserMapper userMapper;

    @Autowired
    public StrategyService(IUserService userService,
                           StrategyMapper strategyMapper,
                           StrategyTargetMapper strategyTargetMapper,
                           StrategyPointMapper strategyPointMapper,
                           LampMapper lampMapper,
                           RegionMapper regionMapper,
                           ElectricityDispositionBoxMapper electricityDispositionBoxMapper,
                           ProjectConfig projectConfig,
                           ObjectMapper objectMapper,
                           IOneNetService oneNetService,
                           OneNetResourceMapper oneNetResourceMapper,
                           OrganizationMapper organizationMapper,
                           UserMapper userMapper) {
        this.userService = userService;
        this.strategyMapper = strategyMapper;
        this.strategyTargetMapper = strategyTargetMapper;
        this.strategyPointMapper = strategyPointMapper;
        this.lampMapper = lampMapper;
        this.regionMapper = regionMapper;
        this.electricityDispositionBoxMapper = electricityDispositionBoxMapper;
        this.projectConfig = projectConfig;
        this.objectMapper = objectMapper;
        this.oneNetService = oneNetService;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.organizationMapper = organizationMapper;
        this.userMapper = userMapper;
    }

    @Transactional(transactionManager = "znldTransactionManager")
    @Override
    public Map<Target, ArrayList<Pair<String, BaseResult>>> newLampStrategy(LampStrategy strategy) {
        try {
            if(this.isLampParamError(strategy)){
                return null;
            }
            var from = strategy.getFrom(this.projectConfig.zoneId);
            var to = strategy.getTo(this.projectConfig.zoneId);
            var fromDate = from.toLocalDate();
            var fromTime = from.toLocalTime();
            var toDate = to.toLocalDate();
            var toTime = to.toLocalTime();
            // 保存下发记录
            var s = new StrategyModel();
            s.name = strategy.name;
            s.fromDate = fromDate;
            s.toDate = toDate;
            s.fromTime = fromTime;
            s.toTime = toTime;
            s.autoGenerateTime = false;
            s.type = Strategy.LAMP;
            s.userId = strategy.userId;
            s.organizationId = strategy.organId;
            if (this.strategyMapper.insert(s) > 0) { // 保存策略
                for (var target : strategy.targets) { // 保存策略关联的对象
                    for (var id : target.ids) {
                        var lampStrategyTarget = new StrategyTargetModel();
                        lampStrategyTarget.targetId = id;
                        lampStrategyTarget.strategyId = s.id;
                        lampStrategyTarget.targetType = target.target;
                        if (this.strategyTargetMapper.insert(lampStrategyTarget) <= 0) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return null;
                        }
                    }
                }
                // 关联时间点，如果存在，之前已经检查了参数
                if (strategy.points != null && strategy.points.size() > 0) {
                    for (var p : strategy.points) {
                        var dateTime = MyDateTime.toLocalDateTime(p.time);
                        assert dateTime != null; // 不可能为空，前面已经判断了
                        var time = dateTime.toLocalTime();
                        var lampStrategyPoint = new StrategyPointModel();
                        lampStrategyPoint.strategyId = s.id;
                        lampStrategyPoint.brightness = p.brightness;
                        lampStrategyPoint.at = time;
                        if (this.strategyPointMapper.insert(lampStrategyPoint) <= 0) { // 保存策略关联的时间点
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return null;
                        }
                    }
                }
            }
            // 发送到onenet
            var map = this.sendToLamp(strategy);
            if (map == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            return map;
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    public List<LampStrategy> getLampStrategies(String organId) {
        var strategies = this.strategyMapper.selectByOrganIdType(organId, Strategy.LAMP);
        return this.getLampStrategies(strategies);
    }

    @Override
    public List<LampStrategy> getLampStrategies(String organId, StrategyStatus status) {
        var strategies = this.strategyMapper.selectByOrganIdTypeStatus(organId, Strategy.LAMP, status);
        return this.getLampStrategies(strategies);
    }

    @Override
    public List<LampStrategy> getLampStrategies(String organId, StrategyStatus status, LocalDate begin, LocalDate end) {
        var strategies = this.strategyMapper.selectByOrganIdTypeStatusBetween(organId, Strategy.LAMP, status, begin, end);
        return this.getLampStrategies(strategies);
    }

    private  ArrayList<LampStrategy> getLampStrategies(List<StrategyModel> strategies) {
        if (strategies == null || strategies.isEmpty()) return null;
        var list = new ArrayList<LampStrategy>(strategies.size());
        for (var s : strategies) {
            var targets = this.strategyTargetMapper.selectByStrategyId(s.id); // 这个对象集合里头可能包含单个对象或者区域（街道）
            if (targets == null || targets.isEmpty()) continue;
            var tmp = new LampStrategy();
            tmp.id = s.id;
            tmp.targets = new ArrayList<>();
            tmp.name = s.name;
            tmp.userId = s.userId;
            tmp.organId = s.organizationId;
            tmp.from = MyDateTime.toTimestamp(LocalDateTime.of(s.fromDate, s.fromTime));
            tmp.to = MyDateTime.toTimestamp(LocalDateTime.of(s.toDate, s.toTime));
            var singleTarget = new StrategyTarget();
            singleTarget.target = Target.SINGLE;
            singleTarget.ids = new ArrayList<>();
            var regionTarget = new StrategyTarget();
            regionTarget.target = Target.REGION;
            regionTarget.ids = new ArrayList<>();
            for (var target : targets) {
                if (target.targetType == Target.REGION) { // 这是区域的id
                    regionTarget.ids.add(target.targetId);
                } else { // 否则就是单个设备的id，这里这个设备就是路灯
                    singleTarget.ids.add(target.targetId);
                }
            }
            // 现在已经将区域和单个设备分开了
            if (!singleTarget.ids.isEmpty()) tmp.targets.add(singleTarget);
            if (!regionTarget.ids.isEmpty()) tmp.targets.add(regionTarget);
            // 把照明灯策略关联的时间点加上去
            var points = this.strategyPointMapper.selectByStrategyId(s.id);
            tmp.points = points.stream().map(p -> {
                ZoneId zoneId = null;
                try {
                    zoneId = ZoneId.of(this.projectConfig.zoneId);
                } catch (Exception ex) {
                    log.debug(ex.getMessage());
                    zoneId = ZoneId.systemDefault();
                }
                var zoneOffset = Instant.now().atZone(zoneId).getOffset();
                var localDateTime = LocalDateTime.of(LocalDate.now(), p.at); // 为了转换方便，加了LocalDate.now()，前端收到这个后应该忽略date部分
                var timestamp = localDateTime.toInstant(zoneOffset).toEpochMilli();
                return new LampStrategy.Point(timestamp, p.brightness);
            }).collect(Collectors.toList());
            // 把这个策略的具体情况加入到结果集合中
            list.add(tmp);
        }
        return list;
    }

    private Map<Target, ArrayList<Pair<String, BaseResult>>> sendToBox(Command cmd) {
        try {
            var msg = cmd.toMessage(); // 得到硬件能识别的指令
            var resource = this.oneNetResourceMapper.selectByResourceName("单灯下发");
            if (resource == null) {
                log.error("在oneNetResource找不到单灯下发的id");
                return null;
            }
            var singleResults = new ArrayList<Pair<String, Future<BaseResult>>>();
            var regionResults = new ArrayList<Pair<String, Future<BaseResult>>>();
            for (var t : cmd.targets) {
                if (t.target == Target.SINGLE) {
                    for (var id : t.ids) {
                        var box = this.electricityDispositionBoxMapper.selectById(id);
                        var params = new CommandParams(box.imei,
                                OneNetKey.from(resource.objId, resource.objInstId, resource.resId),
                                this.objectMapper.writeValueAsString(msg));
                        singleResults.add(new Pair<>(id, this.oneNetService.executeAsync(params)));
                        log.debug("json为" + this.objectMapper.writeValueAsString(msg));
                    }
                } else { // 是针对街道区域的
                    for (var id : t.ids) {
                        var boxes = this.regionMapper.selectBoxesByRegionId(id);
                        if (boxes == null || boxes.isEmpty()) continue;
                        for (var box : boxes) {
                            var params = new CommandParams(box.imei,
                                    OneNetKey.from(resource.objId, resource.objInstId, resource.resId),
                                    this.objectMapper.writeValueAsString(msg));
                            regionResults.add(new Pair<>(id, this.oneNetService.executeAsync(params)));
                            log.debug("json为" + this.objectMapper.writeValueAsString(msg));
                        }
                    }
                }
            }
            var singles = new ArrayList<Pair<String, BaseResult>>();
            var regions = new ArrayList<Pair<String, BaseResult>>();
            var map = new HashMap<Target, ArrayList<Pair<String, BaseResult>>>();
            map.put(Target.SINGLE, singles);
            map.put(Target.REGION, regions);
            for (var pair : singleResults) {
                singles.add(new Pair<>(pair.one, pair.two.get()));
            }
            for (var pair : regionResults) {
                regions.add(new Pair<>(pair.one, pair.two.get()));
            }
            return map;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    private Map<Target, ArrayList<Pair<String, BaseResult>>> sendToLamp(Command cmd) {
        try {
            var msg = cmd.toMessage(); // 得到硬件能识别的指令
            var resource = this.oneNetResourceMapper.selectByResourceName("单灯下发");
            if (resource == null) {
                log.error("在oneNetResource找不到单灯下发的id");
                return null;
            }
            var singleResults = new ArrayList<Pair<String, Future<BaseResult>>>();
            var regionResults = new ArrayList<Pair<String, Future<BaseResult>>>();
            for (var t : cmd.targets) {
                if (t.target == Target.SINGLE) {
                    for (var id : t.ids) {
                        var lamp = this.lampMapper.selectById(id);
                        var params = new CommandParams(lamp.imei,
                                OneNetKey.from(resource.objId, resource.objInstId, resource.resId),
                                this.objectMapper.writeValueAsString(msg));
                        singleResults.add(new Pair<>(id, this.oneNetService.executeAsync(params)));
                        log.debug("json为" + this.objectMapper.writeValueAsString(msg));
                    }
                } else { // 是针对街道区域的
                    for (var id : t.ids) {
                        var lamps = this.regionMapper.selectLampsByRegionId(id);
                        if (lamps == null || lamps.isEmpty()) continue;
                        for (var lamp : lamps) {
                            var params = new CommandParams(lamp.imei,
                                    OneNetKey.from(resource.objId, resource.objInstId, resource.resId),
                                    this.objectMapper.writeValueAsString(msg));
                            regionResults.add(new Pair<>(id, this.oneNetService.executeAsync(params)));
                            log.debug("json为" + this.objectMapper.writeValueAsString(msg));
                        }
                    }
                }
            }
            var singles = new ArrayList<Pair<String, BaseResult>>();
            var regions = new ArrayList<Pair<String, BaseResult>>();
            var map = new HashMap<Target, ArrayList<Pair<String, BaseResult>>>();
            map.put(Target.SINGLE, singles);
            map.put(Target.REGION, regions);
            for (var pair : singleResults) {
                singles.add(new Pair<>(pair.one, pair.two.get()));
            }
            for (var pair : regionResults) {
                regions.add(new Pair<>(pair.one, pair.two.get()));
            }
            return map;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    @Override
    public Map<Target, ArrayList<Pair<String, BaseResult>>> newBoxStrategy(BoxStrategy strategy) {
        try {
            if(this.isBoxParamError(strategy)){
                return null;
            }
            var from = strategy.getFrom(this.projectConfig.zoneId);
            var to = strategy.getTo(this.projectConfig.zoneId);
            var fromDate = from.toLocalDate();
            var fromTime = from.toLocalTime();
            var toDate = to.toLocalDate();
            var toTime = to.toLocalTime();
            // 保存下发记录
            var s = new StrategyModel();
            s.name = strategy.name;
            s.fromDate = fromDate;
            s.toDate = toDate;
            s.fromTime = fromTime;
            s.toTime = toTime;
            s.autoGenerateTime = false;
            s.type = Strategy.ELECTRICITY_DISPOSITION_BOX;
            s.userId = strategy.userId;
            s.organizationId = strategy.organId;
            if (this.strategyMapper.insert(s) > 0) { // 保存策略
                for (var target : strategy.targets) { // 保存策略关联的对象
                    for (var id : target.ids) {
                        var lampStrategyTarget = new StrategyTargetModel();
                        lampStrategyTarget.targetId = id;
                        lampStrategyTarget.strategyId = s.id;
                        lampStrategyTarget.targetType = target.target;
                        if (this.strategyTargetMapper.insert(lampStrategyTarget) <= 0) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return null;
                        }
                    }
                }
            }
            // 发送到onenet
            var map = this.sendToLamp(strategy);
            if (map == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            return map;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    public List<BoxStrategy> getBoxStrategies(String organId) {
        var strategies = this.strategyMapper.selectByOrganIdType(organId, Strategy.ELECTRICITY_DISPOSITION_BOX);
        if (strategies == null || strategies.isEmpty()) return null;
        var list = new ArrayList<BoxStrategy>(strategies.size());
        for (var s : strategies) {
            var targets = this.strategyTargetMapper.selectByStrategyId(s.id); // 这个对象集合里头可能包含单个对象或者区域（街道）
            if (targets == null || targets.isEmpty()) continue;
            var tmp = new BoxStrategy();
            tmp.targets = new ArrayList<>();
            tmp.name = s.name;
            tmp.userId = s.userId;
            tmp.organId = s.organizationId;
            tmp.from = MyDateTime.toTimestamp(LocalDateTime.of(s.fromDate, s.fromTime));
            tmp.to = MyDateTime.toTimestamp(LocalDateTime.of(s.toDate, s.toTime));
            var singleTarget = new StrategyTarget();
            singleTarget.target = Target.SINGLE;
            singleTarget.ids = new ArrayList<>();
            var regionTarget = new StrategyTarget();
            regionTarget.target = Target.REGION;
            regionTarget.ids = new ArrayList<>();
            for (var target : targets) {
                if (target.targetType == Target.REGION) { // 这是区域的id
                    regionTarget.ids.add(target.targetId);
                } else { // 否则就是单个设备的id，这里这个设备就是路灯
                    singleTarget.ids.add(target.targetId);
                }
            }
            // 现在已经将区域和单个设备分开了
            if (!singleTarget.ids.isEmpty()) tmp.targets.add(singleTarget);
            if (!regionTarget.ids.isEmpty()) tmp.targets.add(regionTarget);
            // 把这个策略的具体情况加入到结果集合中
            list.add(tmp);
        }
        return list;
    }

    @Override
    public Map<Target, ArrayList<Pair<String, BaseResult>>> newLampManual(ManualStrategy strategy) {
        try {
            if(this.isLampParamError(strategy)){
                return null;
            }
            // 直接发送到onenet
            return this.sendToLamp(strategy);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    @Override
    public Map<Target, ArrayList<Pair<String, BaseResult>>> newBoxManual(ManualStrategy strategy) {
        try {
            if(this.isBoxParamError(strategy)){
                return null;
            }
            // 直接发送到onenet
            return this.sendToBox(strategy);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    private boolean isBoxParamError(Command cmd){
        if (cmd == null) {
            log.debug("传参为空");
            return true;
        }
        if (!cmd.isValidForInsert()) {
            return true;
        }
        for (var t : cmd.targets) {
            for (var id : t.ids) {
                if (t.target == Target.REGION) {
                    if (this.regionMapper.selectById(id) == null) {
                        log.debug("target中的id集合，指定的类型为区域（街道），但其中id[" + id + "]在区域表中不存在");
                        return true;
                    }
                } else {
                    if (this.electricityDispositionBoxMapper.selectById(id) == null) {
                        log.debug("target中的id集合，指定的类型为非区域（街道）即单个配电箱，但其中id[" + id + "]在配电箱表中不存在");
                        return true;
                    }
                }
            }
        }
        var organ = this.organizationMapper.selectById(cmd.organId);
        if (organ == null) {
            log.error("组织不存在");
            return true;
        }
        var user = this.userMapper.selectById(cmd.userId);
        if (user == null) {
            log.error("用户不存在");
            return true;
        }
        if (!user.organizationId.equals(organ.id)) {
            log.error("指定的组织id[" + cmd.organId + "]和用户id[" + cmd.userId + "]不符");
            return true;
        }
        return false;
    }

    private boolean isLampParamError(Command cmd){
        if (cmd == null) {
            log.debug("传参为空");
            return true;
        }
        if (!cmd.isValidForInsert()) {
            return true;
        }
        for (var t : cmd.targets) {
            for (var id : t.ids) {
                if (t.target == Target.REGION) {
                    if (this.regionMapper.selectById(id) == null) {
                        log.debug("target中的id集合，指定的类型为区域（街道），但其中id[" + id + "]在区域表中不存在");
                        return true;
                    }
                } else {
                    if (this.lampMapper.selectById(id) == null) {
                        log.debug("target中的id集合，指定的类型为非区域（街道）即单个路灯，但其中id[" + id + "]在路灯表中不存在");
                        return true;
                    }
                }
            }
        }
        var organ = this.organizationMapper.selectById(cmd.organId);
        if (organ == null) {
            log.error("组织不存在");
            return true;
        }
        var user = this.userMapper.selectById(cmd.userId);
        if (user == null) {
            log.error("用户不存在");
            return true;
        }
        if (!user.organizationId.equals(organ.id)) {
            log.error("指定的组织id[" + cmd.organId + "]和用户id[" + cmd.userId + "]不符");
            return true;
        }
        return false;
    }

    @Override
    public Map<Target, ArrayList<Pair<String, BaseResult>>> newLampManualBrightness(ManualBrightnessStrategy strategy) {
        try {
            if(this.isLampParamError(strategy)){
                return null;
            }
            // 直接发送到onenet
            return this.sendToLamp(strategy);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    @Override
    public void processPendingStrategy(String organId) {
        ZoneId zone = ZoneId.systemDefault();
        if(!MyString.isEmptyOrNull(this.projectConfig.zoneId)) {
            zone = ZoneId.of(this.projectConfig.zoneId);
        }
        var strategies = this.getLampStrategies(organId, StrategyStatus.READY);
        var now = LocalDate.now(zone);
        for(var strategy : strategies) {
            var model = this.strategyMapper.selectById(strategy.id);
            if(model.toDate.isBefore(now)) {
                log.debug("这个id["+strategy.id+"]的策略已经过期，关闭");
                model.status = StrategyStatus.EXPIRED; // 已经过期关闭这条记录
                this.strategyMapper.update(model);
            }
            //log.debug(Period.between(model.fromDate, now).getDays());
            if(Period.between(model.fromDate, now).getDays() <= 1) { // 如果开始日期距离现在小于等于1天，是时候发送到硬件了
                log.debug("这个id["+strategy.id+"]的策略距离开始日期小于等于1天，将策略下发到硬件，并更新策略状态");
                // 通过onenet发送
                this.sendToLamp(strategy);
                // 把状态更新为正在运行
                model.status = StrategyStatus.RUNNING;
                this.strategyMapper.update(model);
            }
        }
    }
}
