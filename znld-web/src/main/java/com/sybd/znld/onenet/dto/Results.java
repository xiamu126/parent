package com.sybd.znld.onenet.dto;

import com.sybd.znld.onenet.dto.Result;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Results {
    private List<Result> results = new ArrayList<>();
}
