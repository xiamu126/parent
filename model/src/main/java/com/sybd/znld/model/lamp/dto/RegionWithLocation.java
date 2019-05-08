package com.sybd.znld.model.lamp.dto;

import com.sybd.znld.model.lamp.RegionModel;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegionWithLocation extends RegionModel {
    public String longitude; // 经度
    public String latitude; // 纬度
}
