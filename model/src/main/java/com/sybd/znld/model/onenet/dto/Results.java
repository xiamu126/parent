package com.sybd.znld.model.onenet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Results {
    public List<Result> results = new ArrayList<>();
    public Results(List<Result> results) {
        this.results = results;
    }
}
