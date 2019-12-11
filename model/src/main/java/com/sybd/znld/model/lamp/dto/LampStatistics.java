package com.sybd.znld.model.lamp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class LampStatistics {
    public Long T; // 时间戳
    public Integer B; // 亮度
    public Double V ; // 电压
    public List<Double> I; // 电流，电流1总电流，电流2单灯电流，电流3电子屏电流
    public List<Double> PP; // 有功功率
    public List<Double> PQ;  // 无功功率
    public List<Double> PS; // 视在功率
    public List<Double> EP; // 有功电能
    public List<Double> EQ; // 无功电能
    public List<Double> ES; // 视在电能
    public Double HZ; // 频率
    public Double TP; // 温度
    public Double HU; // 湿度
    public Double X; // X倾斜角度
    public Double Y; // Y倾斜角度
    public List<Integer> JDQ; // 继电器开关，0代表关，1代表关
}
