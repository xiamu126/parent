package com.sybd.znld.model.lamp;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MiniStarTaskModel implements Serializable {
    public Integer id;
    public String areaId;
    public String userId;
    public Integer areaType; // 0 表示街道区域 1表示单个路灯
    public LocalDateTime beginTime;
    public LocalDateTime endTime;
    public Integer status; // 0 表示等待中 1表示已完成
    public String organizationId;
    public String cmd;
    public String effectType;
    public String colors;
    public Integer speed;
    public Integer brightness;
    public String title;

    public static class AreaType{
        public static final Integer REGION = 0;
        public static final Integer DEVICE = 1;
    }

    public static class TaskStatus{
        public static final Integer WAITING = 0;
        public static final Integer FINISHED = 1;
    }
}
