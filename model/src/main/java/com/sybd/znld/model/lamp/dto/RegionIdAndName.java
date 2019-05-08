package com.sybd.znld.model.lamp.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegionIdAndName {
    public String id;
    public String name;
    public String longitude; // 经度
    public String latitude; // 纬度
}
