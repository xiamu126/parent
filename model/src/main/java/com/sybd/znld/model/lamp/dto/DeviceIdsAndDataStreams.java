package com.sybd.znld.model.lamp.dto;

import com.sybd.znld.model.IValid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class DeviceIdsAndDataStreams implements IValid {
    public List<Integer> deviceIds;
    public List<String> dataStreams;

    @Override
    public boolean isValid() {
        if(this.deviceIds == null || deviceIds.isEmpty()) return false;
        return this.dataStreams != null && !this.dataStreams.isEmpty();
    }
}
