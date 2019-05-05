package com.sybd.znld.web.controller.region;

import com.sybd.znld.web.controller.region.dto.RegionResult;

import javax.servlet.http.HttpServletRequest;

public interface IRegionController {
    RegionResult getAllRegionWithValidLamp(String organId, HttpServletRequest request);
}
