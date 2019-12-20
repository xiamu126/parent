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
    public List<Integer> RL; // 继电器开关，0代表关，1代表关
    public Double Ta; // 大气温度
    public Double Ua; // 大气湿度
    public Double PM25;
    public Double PM10;
    public Double CO;
    public Double NO2;
    public Double SO2;
    public Double O3;
    public Double lat; // 纬度
    public Double lon; // 经度
    public Double hgt; // 高度
    public Double spd; // 速度
    public Integer stn; // 卫星数量
    public Double hddp; // 水平位置相对精度
}
