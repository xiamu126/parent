package com.sybd.znld.model.lamp.dto;

import java.util.List;
import java.util.Objects;

public class RegionTree {
    public String id;
    public String label;
    public List<RegionTree> children;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionTree that = (RegionTree) o;
        return id.equals(that.id) &&
                label.equals(that.label) &&
                Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, children);
    }

    public RegionTree(){}
    public RegionTree(String id, String label, List<RegionTree> children){
        this.id = id;
        this.label = label;
        this.children = children;
    }
}
