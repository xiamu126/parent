package com.sybd.znld.light.service;

import com.sybd.znld.light.control.dto.NewBoxStrategy;
import com.sybd.znld.light.control.dto.NewLampStrategy;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.model.lamp.LampStrategyModel;
import com.sybd.znld.model.lamp.LampStrategyPointModel;
import com.sybd.znld.model.lamp.LampStrategyTargetModel;
import com.sybd.znld.model.lamp.Strategy;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Service
public class StrategyService implements IStrategyService {
    private final IUserService userService;
    private final LampStrategyMapper lampStrategyMapper;
    private final LampStrategyTargetMapper lampStrategyTargetMapper;
    private final LampStrategyPointMapper lampStrategyPointMapper;
    private final LampMapper lampMapper;
    private final ElectricityDispositionBoxMapper electricityDispositionBoxMapper;

    @Autowired
    public StrategyService(IUserService userService,
                           LampStrategyMapper lampStrategyMapper,
                           LampStrategyTargetMapper lampStrategyTargetMapper,
                           LampStrategyPointMapper lampStrategyPointMapper,
                           LampMapper lampMapper,
                           ElectricityDispositionBoxMapper electricityDispositionBoxMapper) {
        this.userService = userService;
        this.lampStrategyMapper = lampStrategyMapper;
        this.lampStrategyTargetMapper = lampStrategyTargetMapper;
        this.lampStrategyPointMapper = lampStrategyPointMapper;
        this.lampMapper = lampMapper;
        this.electricityDispositionBoxMapper = electricityDispositionBoxMapper;
    }

    private boolean isStrategyValid(com.sybd.znld.light.control.dto.Strategy strategy){
        if(MyString.isEmptyOrNull(strategy.name)){ // 策略名称可以重复
            log.debug("策略名称为空");
            return false;
        }
        if(strategy.ids == null || strategy.ids.isEmpty() || strategy.ids.stream().anyMatch(MyString::isAnyEmptyOrNull)){
            log.debug("id为空");
            return false;
        }
        for(var id : strategy.ids){
            if(this.lampMapper.selectById(id) == null){
                log.debug("指定id["+id+"]不存在");
                return false;
            }
        }
        var to = MyDateTime.toLocalDateTime(strategy.to, ZoneId.of("Asia/Shanghai"));
        var toDate = to.toLocalDate();
        if(toDate.isBefore(LocalDate.now())){
            log.debug("指定的截止日期["+MyDateTime.toString(toDate, MyDateTime.FORMAT4)+"]为过去日期");
            return false;
        }
        return true;
    }

    @Transactional(transactionManager = "znldTransactionManager")
    @Override
    public boolean newLampStrategy(NewLampStrategy strategy) {
        if(strategy == null){
            log.debug("策略为空");
            return false;
        }
        if(MyString.isEmptyOrNull(strategy.name)){ // 策略名称可以重复
            log.debug("策略名称为空");
            return false;
        }
        if(strategy.ids == null || strategy.ids.isEmpty() || strategy.ids.stream().anyMatch(MyString::isAnyEmptyOrNull)){
            log.debug("id为空");
            return false;
        }
        for(var id : strategy.ids){
            if(this.lampMapper.selectById(id) == null){
                log.debug("指定id["+id+"]不存在");
                return false;
            }
        }
        var from = MyDateTime.toLocalDateTime(strategy.from, ZoneId.of("Asia/Shanghai"));
        var to = MyDateTime.toLocalDateTime(strategy.to, ZoneId.of("Asia/Shanghai"));
        var fromDate = from.toLocalDate();
        var fromTime = from.toLocalTime();
        var toDate = to.toLocalDate();
        var toTime = to.toLocalTime();
        if(toDate.isBefore(LocalDate.now())){
            log.debug("指定的截止日期["+MyDateTime.toString(toDate, MyDateTime.FORMAT4)+"]为过去日期");
            return false;
        }
        var userId = strategy.userId;
        if(MyString.isEmptyOrNull(userId)){
            log.debug("用户id为空");
            return false;
        }
        var user = this.userService.getUserById(userId);
        if(user == null){
            log.debug("用户不存在");
            return false;
        }
        // 如果存在时间点
        if(strategy.points != null && strategy.points.size() > 0){
            for(var p : strategy.points){
                if(p.brightness < 0 || p.brightness > 100){
                    log.debug("时间点关联的亮度值["+p.brightness+"]非法");
                    return false;
                }
                var dateTime = MyDateTime.toLocalDateTime(p.time);
                if(dateTime == null){
                    log.debug("时间点["+p.time+"]错误");
                    return false;
                }
            }
        }
        // 保存下发记录
        var lampStrategy = new LampStrategyModel();
        lampStrategy.name = strategy.name;
        lampStrategy.fromDate = fromDate;
        lampStrategy.toDate = toDate;
        lampStrategy.fromTime = fromTime;
        lampStrategy.toTime = toTime;
        lampStrategy.autoGenerateTime = false;
        lampStrategy.type = Strategy.LAMP;
        if(this.lampStrategyMapper.insert(lampStrategy) > 0){ // 保存策略
            for(var id : strategy.ids){
                var lampStrategyTarget = new LampStrategyTargetModel();
                lampStrategyTarget.targetId = id;
                lampStrategyTarget.lampStrategyId = lampStrategy.id;
                if(this.lampStrategyTargetMapper.insert(lampStrategyTarget) <= 0){ // 保存策略关联的对象
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return false;
                }
            }
            // 关联时间点
            if(strategy.points != null && strategy.points.size() > 0){
                for(var p : strategy.points){
                    var dateTime = MyDateTime.toLocalDateTime(p.time);
                    assert dateTime != null; // 不可能为空，前面已经判断了
                    var time = dateTime.toLocalTime();
                    var lampStrategyPoint = new LampStrategyPointModel();
                    lampStrategyPoint.lampStrategyId = lampStrategy.id;
                    lampStrategyPoint.brightness = p.brightness;
                    lampStrategyPoint.at = time;
                    if(this.lampStrategyPointMapper.insert(lampStrategyPoint) <= 0){ // 保存策略关联的时间点
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
    public boolean newBoxStrategy(NewBoxStrategy strategy) {
        if(strategy == null){
            log.debug("策略为空");
            return false;
        }
        if(MyString.isEmptyOrNull(strategy.name)){ // 策略名称可以重复
            log.debug("策略名称为空");
            return false;
        }
        if(strategy.ids == null || strategy.ids.isEmpty() || strategy.ids.stream().anyMatch(MyString::isAnyEmptyOrNull)){
            log.debug("id为空");
            return false;
        }
        for(var id : strategy.ids){
            if(this.electricityDispositionBoxMapper.selectById(id) == null){
                log.debug("指定id["+id+"]不存在");
                return false;
            }
        }
        var from = MyDateTime.toLocalDateTime(strategy.from, ZoneId.of("Asia/Shanghai"));
        var to = MyDateTime.toLocalDateTime(strategy.to, ZoneId.of("Asia/Shanghai"));
        var fromDate = from.toLocalDate();
        var fromTime = from.toLocalTime();
        var toDate = to.toLocalDate();
        var toTime = to.toLocalTime();
        if(toDate.isBefore(LocalDate.now())){
            log.debug("指定的截止日期["+MyDateTime.toString(toDate, MyDateTime.FORMAT4)+"]为过去日期");
            return false;
        }
        var userId = strategy.userId;
        if(MyString.isEmptyOrNull(userId)){
            log.debug("用户id为空");
            return false;
        }
        var user = this.userService.getUserById(userId);
        if(user == null){
            log.debug("用户不存在");
            return false;
        }
        // 保存下发记录
        var lampStrategy = new LampStrategyModel();
        lampStrategy.name = strategy.name;
        lampStrategy.fromDate = fromDate;
        lampStrategy.toDate = toDate;
        lampStrategy.fromTime = fromTime;
        lampStrategy.toTime = toTime;
        lampStrategy.autoGenerateTime = false;
        lampStrategy.type = Strategy.ELECTRICITY_DISPOSITION_BOX;
        if(this.lampStrategyMapper.insert(lampStrategy) > 0){ // 保存策略
            for(var id : strategy.ids){
                var lampStrategyTarget = new LampStrategyTargetModel();
                lampStrategyTarget.targetId = id;
                lampStrategyTarget.lampStrategyId = lampStrategy.id;
                if(this.lampStrategyTargetMapper.insert(lampStrategyTarget) <= 0){ // 保存策略关联的对象
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return false;
                }
            }
        }
        // 发送到onenet
        return true;
    }
}
