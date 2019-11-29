package com.sybd.znld.light.control.dto;

import com.sybd.znld.util.MyDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.util.List;

@Slf4j
@ToString(callSuper = true)
public class LampStrategy extends BaseStrategy {
    public List<Point> points;

    @Override
    public boolean isValidForInsert(String zoneId) {
        // 当关联的时间点集合为非空时，需要判断里面包含的内容是否合法
        if(this.points != null && !this.points.isEmpty()){
            ZoneId zone = null;
            try{
                zone = ZoneId.of(zoneId);
            }catch (Exception ex){
                log.debug(ex.getMessage());
                zone = ZoneId.systemDefault();
            }
            for(var p : this.points){
                try{
                    MyDateTime.toLocalDateTime(p.time, zone);
                }catch (Exception ex){
                    log.debug("指定的时间戳["+p.time+"]无法转为LocalDateTime");
                    log.debug(ex.getMessage());
                    return false;
                }
                if(p.brightness == null || p.brightness < 0 || p.brightness > 100){
                    log.debug("指定的亮度百分比["+p.brightness+"]错误");
                    return false;
                }
            }
        }
        return super.isValidForInsert(zoneId);
    }

    @NoArgsConstructor @AllArgsConstructor
    public static class Point {
        public Long time; // 特定的时间点
        public Integer brightness; // 特定的亮度
    }
}
