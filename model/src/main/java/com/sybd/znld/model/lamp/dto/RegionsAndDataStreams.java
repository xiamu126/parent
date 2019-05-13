package com.sybd.znld.model.lamp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class RegionsAndDataStreams {
    public List<String> regions;
    public List<String> dataStreams;
}
