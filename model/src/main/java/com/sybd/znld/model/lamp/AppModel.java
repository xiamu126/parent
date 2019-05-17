package com.sybd.znld.model.lamp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AppModel implements Serializable {
    public String id;
    public String name;
    public Integer versionCode;
    public String url;
    public String description;
}
