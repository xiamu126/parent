package com.sybd.znld.onenet.dto;

import com.sybd.znld.onenet.dto.Result;

import java.util.ArrayList;
import java.util.List;

public class Results {
    public List<Result> results = new ArrayList<>();

    public Results(List<Result> results) {
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
