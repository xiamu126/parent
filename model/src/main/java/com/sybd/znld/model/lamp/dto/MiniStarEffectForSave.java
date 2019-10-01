package com.sybd.znld.model.lamp.dto;

import javax.validation.constraints.*;

public class MiniStarEffectForSave {
    @NotEmpty
    public String name;
    @NotEmpty
    public String type;
    @NotEmpty
    public String colors;
    @Positive
    @NotNull
    public Integer speed;
    @Min(0) @Max(100)
    public Integer brightness;
}
