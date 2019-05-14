package com.sybd.znld.model.lamp.dto;

import com.sybd.znld.model.IValid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class RegionsAndDataStreams implements IValid {
    public List<String> regions;
    public List<String> dataStreams;

    @Override
    public boolean isValid() {
        if(this.regions == null || this.regions.isEmpty()) return false;
        return this.dataStreams != null && !this.dataStreams.isEmpty();
    }
}
