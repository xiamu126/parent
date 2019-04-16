package com.sybd.znld.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class LampResourceModel implements Serializable {
    public String id;
    public String lampId;
    public String oneNetResourceId;
}
