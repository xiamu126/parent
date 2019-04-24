package com.sybd.znld.controller.resource;

import com.sybd.znld.controller.resource.dto.CheckedResourcesResult;

import javax.servlet.http.HttpServletRequest;

public interface IResourceController {
    CheckedResourcesResult getCheckedResources(Integer deviceId, HttpServletRequest request);
    CheckedResourcesResult getCheckedResources(String organId, HttpServletRequest request);
}
