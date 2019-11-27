package com.sybd.znld.light.control.dto;

import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
public class LampStrategy extends Strategy {
    public List<Point> points;

    public static class Point {
        public Long time; // 特定的时间点
        public Integer brightness; // 特定的亮度
    }
}
