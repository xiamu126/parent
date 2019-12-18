package com.sybd.znld.light.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.light.config.ProjectConfig;
import com.sybd.znld.light.controller.dto.*;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.*;
import com.sybd.znld.model.lamp.dto.Message;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Value("${max-try}")
    private Integer maxTry;
    private final IUserService userService;
    private final LampExecutionMapper lampExecutionMapper;
    private final LampStrategyMapper lampStrategyMapper;
    private final LampMapper lampMapper;
    private final RegionMapper regionMapper;
    private final ElectricityDispositionBoxMapper electricityDispositionBoxMapper;
    private final ProjectConfig projectConfig;
    private final ObjectMapper objectMapper;
    private final IOneNetService oneNetService;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final OrganizationMapper organizationMapper;
    private final UserMapper userMapper;
    private final LampStrategyWaitingMapper lampStrategyWaitingMapper;

    @Autowired
    public StrategyService(IUserService userService,
                           LampStrategyMapper lampStrategyMapper,
                           LampMapper lampMapper,
                           RegionMapper regionMapper,
                           ElectricityDispositionBoxMapper electricityDispositionBoxMapper,
                           ProjectConfig projectConfig,
                           ObjectMapper objectMapper,
                           IOneNetService oneNetService,
                           OneNetResourceMapper oneNetResourceMapper,
                           OrganizationMapper organizationMapper,
                           UserMapper userMapper,
                           LampExecutionMapper lampExecutionMapper,
                           LampStrategyWaitingMapper lampStrategyWaitingMapper) {
        this.userService = userService;
        this.lampStrategyMapper = lampStrategyMapper;
        this.lampExecutionMapper = lampExecutionMapper;
        this.lampMapper = lampMapper;
        this.regionMapper = regionMapper;
        this.electricityDispositionBoxMapper = electricityDispositionBoxMapper;
        this.projectConfig = projectConfig;
        this.objectMapper = objectMapper;
        this.oneNetService = oneNetService;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.organizationMapper = organizationMapper;
        this.userMapper = userMapper;
        this.lampStrategyWaitingMapper = lampStrategyWaitingMapper;
    }

    @Override
    public Boolean isLampStrategyOverlapping(LampStrategy strategy) {
        return null;
    }

    @Override
    public void terminateStrategy(String id) {
    }

    @Transactional(transactionManager = "znldTransactionManager")
    @Override
    public ApiResult newLampStrategy(LampStrategy strategy) {
        try {
            if(!strategy.isValid()) {
                return ApiResult.fail();
            }
            var organ = this.organizationMapper.selectById(strategy.organId);
            if (organ == null) {
                log.error("组织不存在");
                return ApiResult.fail();
            }
            var user = this.userMapper.selectById(strategy.userId);
            if (user == null) {
                log.error("用户不存在");
                return ApiResult.fail();
            }
            if (!user.organizationId.equals(organ.id)) {
                log.error("指定的组织id[" + strategy.organId + "]和用户id[" + strategy.userId + "]不符");
                return ApiResult.fail();
            }
            // 判断下指定的开始结束时间是否和已有的策略有冲突；所谓冲突，即开始时间不能一样
            var from = strategy.getFrom();
            var to = strategy.getTo();
            var fromDate = from.toLocalDate();
            var fromTime = from.toLocalTime();
            var toDate = to.toLocalDate();
            var toTime = to.toLocalTime();
            // 首先，保存策略
            var s = new LampStrategyModel();
            s.name = strategy.name;
            s.fromDate = fromDate;
            s.toDate = toDate;
            s.fromTime = fromTime;
            s.toTime = toTime;
            s.autoGenerateTime = false;
            s.userId = strategy.userId;
            s.organId = strategy.organId;
            s.initBrightness = strategy.initBrightness;
            // 关联时间点，如果存在，之前已经检查了参数
            if (strategy.points != null && strategy.points.size() > 0) {
                for (var i = 0; i < strategy.points.size(); i++) {
                    var p = strategy.points.get(i);
                    var dateTime = MyDateTime.toLocalDateTime(p.time);
                    if(dateTime == null) {
                        return ApiResult.fail("发生错误");
                    }
                    var time = dateTime.toLocalTime();
                    if(i == 0) {
                        s.at1 = time;
                        s.brightness1 = p.brightness;
                    }
                    if(i == 1) {
                        s.at2 = time;
                        s.brightness2 = p.brightness;
                    }
                    if(i == 2) {
                        s.at3 = time;
                        s.brightness3 = p.brightness;
                    }
                    if(i == 3) {
                        s.at4 = time;
                        s.brightness4 = p.brightness;
                    }
                    if(i == 4) {
                        s.at5 = time;
                        s.brightness5 = p.brightness;
                    }
                }
            }
            this.lampStrategyMapper.insert(s);
            return ApiResult.success();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            return ApiResult.fail("发生错误");
        }
    }

    @Override
    public Map<String, BaseApiResult> executeLampStrategy(LampStrategyCmd cmd) {
        if(!cmd.isValid()) {
            log.error("参数错误");
            return null;
        }
        var lampStrategy = this.lampStrategyMapper.selectById(cmd.lampStrategyId);
        if(lampStrategy == null) {
            log.error("指定的策略id["+cmd.lampStrategyId+"]不存在");
            return null;
        }
        var lamps = new ArrayList<LampModel>();
        for(var t : cmd.targets) {
            var lamp = this.lampMapper.selectById(t);
            if(lamp == null) {
                log.error("指定的路灯id["+t+"]不存在");
                return null;
            }
            lamps.add(lamp);
        }
        var results = new HashMap<String, BaseApiResult>();
        for(var lamp : lamps) {
            // 在这里不会查看这个策略的开始时间是否快到了，而是把这个策略放到等待列表中
            var lampStrategyWaitingModel = new LampStrategyWaitingModel();
            lampStrategyWaitingModel.organId = lampStrategy.organId;
            lampStrategyWaitingModel.lampId = lamp.id;
            lampStrategyWaitingModel.lampStrategyId = lampStrategy.id;
            var waitingModels = this.lampStrategyWaitingMapper.selectByLampIdStatus(lamp.id, LampStrategyWaitingModel.Status.UNUSED);
            if(waitingModels != null && !waitingModels.isEmpty()) {
                if(waitingModels.stream().anyMatch(w -> w.lampStrategyId.equals(lampStrategy.id))) {
                    // 这盏路灯上已经有这个策略在等待
                    var baseApiResult = new BaseApiResult();
                    baseApiResult.code = 1;
                    baseApiResult.msg = "这个策略已经在目标对象的等待列表中";
                    results.put(lamp.id, baseApiResult);
                    continue;
                }
            }
            var ret = this.lampStrategyWaitingMapper.insert(lampStrategyWaitingModel);
            var baseApiResult = new BaseApiResult();
            if(ret > 0) {
                baseApiResult.code = 0;
                baseApiResult.msg = "";
            }else {
                baseApiResult.code = 1;
                baseApiResult.msg = "无法将此策略添加到目标对象的等待列表中";
            }
            results.put(lamp.id, baseApiResult);
        }
        return results;
    }

    @Override
    public Map<String, BaseApiResult> executeLampStrategy(LampManualCmd cmd) {
        if(!cmd.isValid()) {
            log.error("参数错误");
            return null;
        }
        var lamps = new ArrayList<LampModel>();
        for(var t : cmd.targets) {
            var lamp = this.lampMapper.selectById(t);
            if(lamp == null) {
                log.error("指定的路灯id["+t+"]不存在");
                return null;
            }
            lamps.add(lamp);
        }
        var list = cmd.toBundleList();
        if(list == null) {
            return null;
        }
        try {
            var msg = new Message<>(Message.Address.LAMP, Message.Model.MANUAL, list);
            var json = this.objectMapper.writeValueAsString(msg);
            log.debug("准备下发到硬件的json为" + json);
            var resource = this.oneNetResourceMapper.selectByResourceName("单灯下发");
            if (resource == null) {
                log.error("在oneNetResource找不到单灯下发的id");
                return null;
            }
            var results = new HashMap<String, BaseApiResult>();
            for(var lamp : lamps) {
                var params = new CommandParams(lamp.imei,
                        OneNetKey.from(resource.objId, resource.objInstId, resource.resId),
                        json);
                var future = this.oneNetService.executeAsync(params);
                var result = future.get();
                results.put(lamp.id, new BaseApiResult(result.errno, ""));
                var tmp = this.lampExecutionMapper.selectByLampId(lamp.id);
                if(tmp == null) {
                    // 如果没有这个路灯的运行状态，新增
                    var model = new LampExecutionModel();
                    model.lampId = lamp.id;
                    model.mode = LampExecutionModel.Mode.MANUAL;
                    model.lampStrategyId = "";
                    if(!result.isOk()) {
                        model.status = LampExecutionModel.Status.FAILED;
                    }else {
                        model.status = LampExecutionModel.Status.SUCCESS;
                    }
                    this.lampExecutionMapper.insert(model);
                } else {
                    // 如果已经有了这个路灯的运行状态，则修改
                    tmp.mode = LampExecutionModel.Mode.MANUAL;
                    tmp.lampStrategyId = "";
                    if(!result.isOk()) {
                        tmp.status = LampExecutionModel.Status.FAILED;
                    }else {
                        tmp.status = LampExecutionModel.Status.SUCCESS;
                    }
                    tmp.tryingCount = 0; // 重置，手动模式没有重试的概念
                    tmp.lastTryingTime = LocalDateTime.of(1973,1,1,0,0,0); // 重置，手动模式没有重试的概念
                    this.lampExecutionMapper.update(tmp);
                }
            }
            return results;
        }catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    @Override
    public List<LampStrategy> getLampStrategies(String organId) {
        var strategies = this.lampStrategyMapper.selectByOrganIdStatus(organId, LampStrategyModel.Status.OK);
        return this.getLampStrategies(strategies);
    }

    @Override
    public List<LampStrategy> getLampStrategies(String organId, LampStrategyModel.Status status) {
        var strategies = this.lampStrategyMapper.selectByOrganIdStatus(organId, status);
        return this.getLampStrategies(strategies);
    }

    private List<LampStrategy> getLampStrategies(List<LampStrategyModel> strategies) {
        if (strategies == null || strategies.isEmpty()) return null;
        return strategies.stream().map(s -> {
            var model = new LampStrategy();
            model.organId = s.organId;
            model.userId = s.userId;
            model.from = MyDateTime.toTimestamp(LocalDateTime.of(s.fromDate, s.fromTime));
            model.to = MyDateTime.toTimestamp(LocalDateTime.of(s.toDate, s.toTime));
            model.initBrightness = s.initBrightness;
            model.name = s.name;
            var points = new ArrayList<LampStrategy.Point>();
            if(s.brightness1 >= 0 && s.brightness1 <= 100) {
                points.add(new LampStrategy.Point(MyDateTime.toTimestamp(s.fromDate, s.at1), s.brightness1)); // 这个时间点以这个亮度亮灯
            }
            if(s.brightness2 >= 0 && s.brightness2 <= 100) {
                points.add(new LampStrategy.Point(MyDateTime.toTimestamp(s.fromDate, s.at2), s.brightness2)); // 这个时间点以这个亮度亮灯
            }
            if(s.brightness3 >= 0 && s.brightness3 <= 100) {
                points.add(new LampStrategy.Point(MyDateTime.toTimestamp(s.fromDate, s.at3), s.brightness3)); // 这个时间点以这个亮度亮灯
            }
            if(s.brightness4 >= 0 && s.brightness4 <= 100) {
                points.add(new LampStrategy.Point(MyDateTime.toTimestamp(s.fromDate, s.at4), s.brightness4)); // 这个时间点以这个亮度亮灯
            }
            if(s.brightness5 >= 0 && s.brightness5 <= 100) {
                points.add(new LampStrategy.Point(MyDateTime.toTimestamp(s.fromDate, s.at5), s.brightness5)); // 这个时间点以这个亮度亮灯
            }
            model.points = points;
            return model;
        }).collect(Collectors.toList());
    }

    @Override
    public void processWaitingStrategies() {
        var waitingModels = this.lampStrategyWaitingMapper.selectByStatus(LampStrategyWaitingModel.Status.UNUSED);
        if(waitingModels != null && !waitingModels.isEmpty()) {
            for(var w : waitingModels) {
                var lampStrategy = this.lampStrategyMapper.selectById(w.lampStrategyId);
                if(lampStrategy == null) {
                    continue;
                }
                if(lampStrategy.toDate.isBefore(LocalDate.now())) {
                    // 这条策略已经过期了
                    continue;
                }
                var result = this.executeLampStrategyForWaiting(w.lampId, w.lampStrategyId);
                if(result.isOk()) {
                    // 处理成功，关闭这条等待策略
                    w.status = LampStrategyWaitingModel.Status.USED;
                    this.lampStrategyWaitingMapper.update(w);
                }
            }
        }
    }

    // 专门针对错误重试的策略执行
    private BaseApiResult executeLampStrategyForFailed(String lampId, String lampStrategyId) {
        if(!MyString.isUuid(lampId) || !MyString.isUuid(lampStrategyId)) {
            log.error("参数错误");
            return BaseApiResult.fail("参数错误");
        }
        var lampStrategy = this.lampStrategyMapper.selectById(lampStrategyId);
        if(lampStrategy == null) {
            log.error("指定的策略id["+lampStrategyId+"]不存在");
            return BaseApiResult.fail("参数错误");
        }
        if(lampStrategy.fromDate == null || lampStrategy.toDate == null) {
            log.error("参数错误");
            return BaseApiResult.fail("参数错误");
        }
        var lamp = this.lampMapper.selectById(lampId);
        if(lamp == null) {
            log.error("指定的路灯id["+lampId+"]不存在");
            return BaseApiResult.fail("参数错误");
        }
        var lampExecutionModel = this.lampExecutionMapper.selectByLampId(lamp.id);
        var list = lampStrategy.toBundleList();
        if(list == null) {
            return BaseApiResult.fail("错误");
        }
        try {
            var msg = new Message<>(Message.Address.LAMP, Message.Model.STRATEGY, list);
            var json = this.objectMapper.writeValueAsString(msg);
            log.debug("处理等待任务，准备下发到硬件的json为" + json);
            var resource = this.oneNetResourceMapper.selectByResourceName("单灯下发");
            if (resource == null) {
                log.error("在oneNetResource找不到单灯下发的id");
                return BaseApiResult.fail("错误");
            }
            var params = new CommandParams(lamp.imei,
                    OneNetKey.from(resource.objId, resource.objInstId, resource.resId),
                    json);
            var future = this.oneNetService.executeAsync(params);
            var result = future.get();
            if (!result.isOk()) {
                lampExecutionModel.status = LampExecutionModel.Status.TRYING;
            } else {
                lampExecutionModel.status = LampExecutionModel.Status.TRYING_SUCCESS;
            }
            if(lampExecutionModel.tryingCount >= this.maxTry) {
                // 达到重试上限
                lampExecutionModel.status = LampExecutionModel.Status.TRYING_FAILED;
                this.lampExecutionMapper.update(lampExecutionModel);
                return BaseApiResult.success();
            }
            lampExecutionModel.tryingCount = lampExecutionModel.tryingCount + 1;
            lampExecutionModel.lastTryingTime = LocalDateTime.now();
            this.lampExecutionMapper.update(lampExecutionModel);
            return BaseApiResult.success();
        }catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            return BaseApiResult.fail("");
        }
    }

    // 不考虑重试的情况，也就是这个执行策略是针对从等待列表中获取并执行的情况
    private BaseApiResult executeLampStrategyForWaiting(String lampId, String lampStrategyId) {
        if(!MyString.isUuid(lampId) || !MyString.isUuid(lampStrategyId)) {
            log.error("参数错误");
            return BaseApiResult.fail("参数错误");
        }
        var lampStrategy = this.lampStrategyMapper.selectById(lampStrategyId);
        if(lampStrategy == null) {
            log.error("指定的策略id["+lampStrategyId+"]不存在");
            return BaseApiResult.fail("参数错误");
        }
        if(lampStrategy.fromDate == null || lampStrategy.toDate == null) {
            log.error("参数错误");
            return BaseApiResult.fail("参数错误");
        }
        var lamp = this.lampMapper.selectById(lampId);
        if(lamp == null) {
            log.error("指定的路灯id["+lampId+"]不存在");
            return BaseApiResult.fail("参数错误");
        }
        var lampExecutionModel = this.lampExecutionMapper.selectByLampId(lamp.id);
        var list = lampStrategy.toBundleList();
        if(list == null) {
            return BaseApiResult.fail("错误");
        }
        try {
            var msg = new Message<>(Message.Address.LAMP, Message.Model.STRATEGY, list);
            var json = this.objectMapper.writeValueAsString(msg);
            log.debug("处理等待任务，准备下发到硬件的json为" + json);
            var resource = this.oneNetResourceMapper.selectByResourceName("单灯下发");
            if (resource == null) {
                log.error("在oneNetResource找不到单灯下发的id");
                return BaseApiResult.fail("错误");
            }
            var params = new CommandParams(lamp.imei,
                    OneNetKey.from(resource.objId, resource.objInstId, resource.resId),
                    json);
            var future = this.oneNetService.executeAsync(params);
            var result = future.get();
            if (lampExecutionModel == null) {
                // 如果没有这个路灯的运行状态，新增
                var model = new LampExecutionModel();
                model.lampId = lamp.id;
                model.mode = LampExecutionModel.Mode.STRATEGY;
                model.lampStrategyId = lampStrategyId;
                if (!result.isOk()) {
                    model.status = LampExecutionModel.Status.FAILED;
                } else {
                    model.status = LampExecutionModel.Status.SUCCESS;
                }
                this.lampExecutionMapper.insert(model);
            } else {
                // 如果已经有了这个路灯的运行状态
                // 不管是从手动模式进入策略模式，还是从一个策略变成另一个策略
                lampExecutionModel.mode = LampExecutionModel.Mode.STRATEGY;
                lampExecutionModel.lampStrategyId = lampStrategyId;
                if (!result.isOk()) {
                    lampExecutionModel.status = LampExecutionModel.Status.FAILED;
                } else {
                    lampExecutionModel.status = LampExecutionModel.Status.SUCCESS;
                }
                lampExecutionModel.tryingCount = 0;
                lampExecutionModel.lastTryingTime = LocalDateTime.of(1973,1,1,0,0,0);
                this.lampExecutionMapper.update(lampExecutionModel);
            }
            return BaseApiResult.success();
        }catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            return BaseApiResult.fail("");
        }
    }

    @Override
    public void processFailedLamps() {
        var failedModels = this.lampExecutionMapper.selectByStatus(LampExecutionModel.Status.FAILED);
        if(failedModels != null && !failedModels.isEmpty()) {
            for(var f : failedModels) {
                this.executeLampStrategyForFailed(f.lampId, f.lampStrategyId);
            }
        }
        var tryingFailedModels = this.lampExecutionMapper.selectByStatus(LampExecutionModel.Status.TRYING);
        if(tryingFailedModels != null && !tryingFailedModels.isEmpty()) {
            for(var t : tryingFailedModels) {
                this.executeLampStrategyForFailed(t.lampId, t.lampStrategyId);
            }
        }
    }
}
