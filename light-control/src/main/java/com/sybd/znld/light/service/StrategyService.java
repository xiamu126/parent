package com.sybd.znld.light.service;

import com.sybd.znld.light.config.ProjectConfig;
import com.sybd.znld.light.controller.dto.*;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.model.lamp.*;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    public StrategyService(IUserService userService,
                           StrategyMapper strategyMapper,
                           StrategyTargetMapper strategyTargetMapper,
                           StrategyPointMapper strategyPointMapper,
                           LampMapper lampMapper,
                           RegionMapper regionMapper,
                           ElectricityDispositionBoxMapper electricityDispositionBoxMapper,
                           ProjectConfig projectConfig) {
        this.userService = userService;
        this.strategyMapper = strategyMapper;
        this.strategyTargetMapper = strategyTargetMapper;
        this.strategyPointMapper = strategyPointMapper;
        this.lampMapper = lampMapper;
        this.regionMapper = regionMapper;
        this.electricityDispositionBoxMapper = electricityDispositionBoxMapper;
        this.projectConfig = projectConfig;
    }

    @Transactional(transactionManager = "znldTransactionManager")
    @Override
    public boolean newLampStrategy(LampStrategy strategy) {
        if(strategy == null){
            log.debug("传参为空");
            return false;
        }
        if(!strategy.isValidForInsert(this.projectConfig.zoneId)){
            return false;
        }
        for(var t : strategy.targets){
            for(var id : t.ids){ // 放心循环，前面的代码已经做了基础检查
                if(t.target == Target.REGION){
                    if(this.regionMapper.selectById(id) == null){
                        log.debug("target中的id集合，指定的类型为区域（街道），但其中id["+id+"]在区域表中不存在");
                        return false;
                    }
                }else{
                    if(this.lampMapper.selectById(id) == null){
                        log.debug("target中的id集合，指定的类型为非区域（街道）即单个路灯，但其中id["+id+"]在路灯表中不存在");
                        return false;
                    }
                }
            }
        }
        var from = strategy.getFrom(this.projectConfig.zoneId);
        var to = strategy.getTo(this.projectConfig.zoneId);
        var fromDate = from.toLocalDate();
        var fromTime = from.toLocalTime();
        var toDate = to.toLocalDate();
        var toTime = to.toLocalTime();
        var userId = strategy.userId;
        var user = this.userService.getUserById(userId);
        if(user == null){
            log.debug("用户不存在");
            return false;
        }
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
        if(this.strategyMapper.insert(s) > 0){ // 保存策略
            for(var target : strategy.targets){ // 保存策略关联的对象
                for(var id : target.ids){
                    var lampStrategyTarget = new StrategyTargetModel();
                    lampStrategyTarget.targetId = id;
                    lampStrategyTarget.strategyId = s.id;
                    lampStrategyTarget.targetType = target.target;
                    if(this.strategyTargetMapper.insert(lampStrategyTarget) <= 0){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return false;
                    }
                }
            }
            // 关联时间点，如果存在，之前已经检查了参数
            if(strategy.points != null && strategy.points.size() > 0){
                for(var p : strategy.points){
                    var dateTime = MyDateTime.toLocalDateTime(p.time);
                    assert dateTime != null; // 不可能为空，前面已经判断了
                    var time = dateTime.toLocalTime();
                    var lampStrategyPoint = new StrategyPointModel();
                    lampStrategyPoint.strategyId = s.id;
                    lampStrategyPoint.brightness = p.brightness;
                    lampStrategyPoint.at = time;
                    if(this.strategyPointMapper.insert(lampStrategyPoint) <= 0){ // 保存策略关联的时间点
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return false;
                    }
                }
            }
        }
        // 发送到onenet
        var msg = strategy.toMessage();
        return true;
    }

    @Override
    public List<LampStrategy> getLampStrategies(String organId) {
        var strategies = this.strategyMapper.selectByOrganIdType(organId, Strategy.LAMP);
        if(strategies == null || strategies.isEmpty()) return null;
        var list = new ArrayList<LampStrategy>(strategies.size());
        for(var s : strategies){
            var targets = this.strategyTargetMapper.selectByStrategyId(s.id); // 这个对象集合里头可能包含单个对象或者区域（街道）
            if(targets == null || targets.isEmpty()) continue;
            var tmp = new LampStrategy();
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
            for(var target : targets){
                if(target.targetType == Target.REGION){ // 这是区域的id
                    regionTarget.ids.add(target.targetId);
                }else { // 否则就是单个设备的id，这里这个设备就是路灯
                    singleTarget.ids.add(target.targetId);
                }
            }
            // 现在已经将区域和单个设备分开了
            if(!singleTarget.ids.isEmpty()) tmp.targets.add(singleTarget);
            if(!regionTarget.ids.isEmpty()) tmp.targets.add(regionTarget);
            // 把照明灯策略关联的时间点加上去
            var points = this.strategyPointMapper.selectByStrategyId(s.id);
            tmp.points = points.stream().map(p -> {
                ZoneId zoneId = null;
                try{
                    zoneId = ZoneId.of(this.projectConfig.zoneId);
                }catch (Exception ex){
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

    @Override
    public boolean newBoxStrategy(BoxStrategy strategy) {
        if(strategy == null){
            log.debug("传参为空");
            return false;
        }
        if(!strategy.isValidForInsert(this.projectConfig.zoneId)){
            return false;
        }
        for(var t : strategy.targets){
            for(var id : t.ids){
                if(t.target == Target.REGION){
                    if(this.regionMapper.selectById(id) == null){
                        log.debug("target中的id集合，指定的类型为区域（街道），但其中id["+id+"]在区域表中不存在");
                        return false;
                    }
                }else{
                    if(this.electricityDispositionBoxMapper.selectById(id) == null){
                        log.debug("target中的id集合，指定的类型为非区域（街道）即单个配电箱，但其中id["+id+"]在配电箱表中不存在");
                        return false;
                    }
                }
            }
        }
        var from = strategy.getFrom(this.projectConfig.zoneId);
        var to = strategy.getTo(this.projectConfig.zoneId);
        var fromDate = from.toLocalDate();
        var fromTime = from.toLocalTime();
        var toDate = to.toLocalDate();
        var toTime = to.toLocalTime();
        if(this.userService.getUserById(strategy.userId) == null){
            log.debug("用户id["+strategy.userId+"]不存在");
            return false;
        }
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
        if(this.strategyMapper.insert(s) > 0){ // 保存策略
            for(var target : strategy.targets){ // 保存策略关联的对象
                for(var id : target.ids){
                    var lampStrategyTarget = new StrategyTargetModel();
                    lampStrategyTarget.targetId = id;
                    lampStrategyTarget.strategyId = s.id;
                    lampStrategyTarget.targetType = target.target;
                    if(this.strategyTargetMapper.insert(lampStrategyTarget) <= 0){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return false;
                    }
                }
            }
        }
        // 发送到onenet
        return true;
    }

    @Override
    public List<BoxStrategy> getBoxStrategies(String organId) {
        var strategies = this.strategyMapper.selectByOrganIdType(organId, Strategy.ELECTRICITY_DISPOSITION_BOX);
        if(strategies == null || strategies.isEmpty()) return null;
        var list = new ArrayList<BoxStrategy>(strategies.size());
        for(var s : strategies){
            var targets = this.strategyTargetMapper.selectByStrategyId(s.id); // 这个对象集合里头可能包含单个对象或者区域（街道）
            if(targets == null || targets.isEmpty()) continue;
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
            for(var target : targets){
                if(target.targetType == Target.REGION){ // 这是区域的id
                    regionTarget.ids.add(target.targetId);
                }else { // 否则就是单个设备的id，这里这个设备就是路灯
                    singleTarget.ids.add(target.targetId);
                }
            }
            // 现在已经将区域和单个设备分开了
            if(!singleTarget.ids.isEmpty()) tmp.targets.add(singleTarget);
            if(!regionTarget.ids.isEmpty()) tmp.targets.add(regionTarget);
            // 把这个策略的具体情况加入到结果集合中
            list.add(tmp);
        }
        return list;
    }

    @Override
    public boolean sendCmdToLamp(ManualStrategy strategy) {
        return false;
    }

    @Override
    public boolean sendCmdToBox(ManualStrategy strategy) {
        return false;
    }

    @Override
    public boolean sendBrightnessCmdToLamp(ManualBrightnessStrategy strategy) {
        return false;
    }
}
