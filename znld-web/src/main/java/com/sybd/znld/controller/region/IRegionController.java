package com.sybd.znld.controller.region;

import com.sybd.znld.controller.region.dto.RegionResult;

import javax.servlet.http.HttpServletRequest;

public interface IRegionController {
    RegionResult getAllRegionWithValidLamp(String organId, HttpServletRequest request);
}
