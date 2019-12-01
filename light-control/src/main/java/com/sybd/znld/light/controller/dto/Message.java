package com.sybd.znld.light.controller.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public List<Pair> s = new ArrayList<>();
    @AllArgsConstructor @NoArgsConstructor
    public static class Pair {
        public float v;
        public float t;
    }
}
