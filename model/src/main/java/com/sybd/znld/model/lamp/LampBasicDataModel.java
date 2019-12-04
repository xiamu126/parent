package com.sybd.znld.model.lamp;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class LampBasicDataModel {
    public float V ; //电压
    public List<Float> I; //电流
    public List<Float> P; //功率
    public List<Float> PF; //功率因素
    public List<Float> KWH; //电能
    public float HZ; //频率
}
