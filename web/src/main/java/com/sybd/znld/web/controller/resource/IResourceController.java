package com.sybd.znld.web.controller.resource;

import com.sybd.znld.web.controller.resource.dto.CheckedResourcesResult;

import javax.servlet.http.HttpServletRequest;

public interface IResourceController {
    CheckedResourcesResult getCheckedResources(Integer deviceId, HttpServletRequest request);
    CheckedResourcesResult getCheckedEnvResources(Integer deviceId, HttpServletRequest request);
    CheckedResourcesResult getCheckedResources(String organId, HttpServletRequest request);
    CheckedResourcesResult getCheckedEnvResources(String organId, HttpServletRequest request);
}
