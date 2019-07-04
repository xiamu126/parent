package com.sybd.znld.web.controller.region;

import com.sybd.znld.web.controller.region.dto.RegionResult;
import com.sybd.znld.web.controller.region.dto.RegionTreeResult;

import javax.servlet.http.HttpServletRequest;

public interface IRegionController {
    RegionResult getAllRegionWithValidLamp(String organId, HttpServletRequest request);
    RegionTreeResult getRegionTreeByOrganId(String organId);
}
