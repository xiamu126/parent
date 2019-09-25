package com.sybd.znld.web.controller.resource;

import com.sybd.znld.web.controller.resource.dto.CheckedResourcesResult;

import javax.servlet.http.HttpServletRequest;

public interface IResourceController {
    CheckedResourcesResult getCheckedResources(Integer deviceId, HttpServletRequest request);
    CheckedResourcesResult getCheckedEnvResources(Integer deviceId, HttpServletRequest request);
    CheckedResourcesResult getCheckedResources(String organId, HttpServletRequest request);
    CheckedResourcesResult getCheckedEnvResources(String organId, HttpServletRequest request);
    CheckedResourcesResult getCheckedEnvResourceByResourceDesc(String organId, String resourceDesc, HttpServletRequest request);
    CheckedResourcesResult getCheckedEnvResourceByResourceDesc(Integer deviceId, String resourceDesc, HttpServletRequest request);
}
