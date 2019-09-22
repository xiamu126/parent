package com.sybd.znld.onenet.controller.region.dto;

import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.dto.RegionTree;

import java.util.List;

public class RegionTreeResult extends BaseApiResult {
    public List<RegionTree> regions;
    public RegionTreeResult(int code, String msg){
        super(code, msg);
        regions = null;
    }
    public RegionTreeResult(int code, String msg, List<RegionTree> result){
        super(code, msg);
        regions = result;
    }
    public static RegionTreeResult fail(String msg){
        return new RegionTreeResult(1, msg);
    }
    public static RegionTreeResult success(List<RegionTree> result){
        return new RegionTreeResult(0, "", result);
    }
}
