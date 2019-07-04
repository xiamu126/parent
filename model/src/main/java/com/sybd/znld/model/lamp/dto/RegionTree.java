package com.sybd.znld.model.lamp.dto;

import java.util.Objects;

public class RegionTree {
    public String id;
    public String name;
    public String parentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionTree that = (RegionTree) o;
        return id.equals(that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parentId);
    }
}
