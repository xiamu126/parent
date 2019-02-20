package com.sybd.znld.dto;

import java.io.Serializable;

public class NameAndOneNetKey implements Serializable{
    public String name;
    public String oneNetKey;

    public NameAndOneNetKey(){}
    public NameAndOneNetKey(String name, String oneNetKey) {
        this.name = name;
        this.oneNetKey = oneNetKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOneNetKey() {
        return oneNetKey;
    }

    public void setOneNetKey(String oneNetKey) {
        this.oneNetKey = oneNetKey;
    }
}
