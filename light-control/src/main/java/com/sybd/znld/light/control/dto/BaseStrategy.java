package com.sybd.znld.light.control.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.IValidForDBInsert;
import com.sybd.znld.model.IValidForDbInsertWithZoneId;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyString;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@ToString
public class BaseStrategy implements IValidForDbInsertWithZoneId {
    public List<StrategyTarget> targets; // 这个target可以是区域的集合或者单个设备的集合，至于是照明灯的id还是配电箱的id，由继承类决定
    public String name; // 策略的名称
    public Long from; // 时间统一以时间戳，这个时间戳里包含日和时
    public Long to;
    @JsonProperty("user_id")
    public String userId; // 是谁新建这个策略的
    @JsonProperty("organ_id")
    public String organId;

    public LocalDateTime getFrom(String zoneId){
        ZoneId zone = null;
        try{
            zone = ZoneId.of(zoneId);
        }catch (Exception ex){
            log.debug(ex.getMessage());
            zone = ZoneId.systemDefault();
        }
        return MyDateTime.toLocalDateTime(this.from, zone);
    }
    public LocalDateTime getTo(String zoneId){
        ZoneId zone = null;
        try{
            zone = ZoneId.of(zoneId);
        }catch (Exception ex){
            log.debug(ex.getMessage());
            zone = ZoneId.systemDefault();
        }
        return MyDateTime.toLocalDateTime(this.to, zone);
    }

    @Override
    public boolean isValidForInsert(String zoneId) {
        if(this.targets == null || this.targets.isEmpty()){
            log.debug("目标集合targets为空");
            return false;
        }
        for(var t : this.targets){
            if(t == null || t.target == null || t.ids == null || t.ids.isEmpty() || t.ids.stream().anyMatch(i->!MyString.isUuid(i))){
                log.debug("目标集合targets包含空值，或ids集合中包含非uuid值");
                return false;
            }
        }
        if(MyString.isEmptyOrNull(name)){// 策略名称可以重复
            log.debug("策略名字非法");
            return false;
        }
        try{
            var from = this.getFrom(zoneId);
            var to = this.getTo(zoneId);
            if(to.isBefore(LocalDateTime.now())){
                log.debug("指定的截止日期["+MyDateTime.toString(to, MyDateTime.FORMAT4)+"]为过去日期");
                return false;
            }
            if(to.isBefore(from)){
                log.debug("指定的截止日期["+MyDateTime.toString(to, MyDateTime.FORMAT1)+"]在开始时间["+MyDateTime.toString(from, MyDateTime.FORMAT1)+"]后面");
                return false;
            }
        }catch (Exception ex){
            log.debug(ex.getMessage());
            return false;
        }
        if(!MyString.isUuid(userId)){
            log.debug("非法的用户id["+this.userId+"]");
            return false;
        }
        if(!MyString.isUuid(this.organId)){
            log.debug("非法的组织id["+this.organId+"]");
            return false;
        }
        return true;
    }
}
