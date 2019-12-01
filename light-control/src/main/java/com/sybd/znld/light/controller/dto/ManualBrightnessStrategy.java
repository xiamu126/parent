package com.sybd.znld.light.controller.dto;

import lombok.ToString;

@ToString
public class ManualBrightnessStrategy extends Command {
    public int brightness;

    @Override
    public boolean isValidForInsert() {
        if(brightness < 0 || brightness > 100) return false;
        return super.isValidForInsert();
    }
}
