package com.sybd.znld.position.model;

public class Point {
    public Double lng;
    public Double lat;
    @Override
    public String toString() {
        return lng+","+lat;
    }
}
