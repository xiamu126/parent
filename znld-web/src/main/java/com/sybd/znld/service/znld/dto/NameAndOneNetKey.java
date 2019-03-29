package com.sybd.znld.service.znld.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class NameAndOneNetKey implements Serializable{
    public String name;
    public String oneNetKey;
}
