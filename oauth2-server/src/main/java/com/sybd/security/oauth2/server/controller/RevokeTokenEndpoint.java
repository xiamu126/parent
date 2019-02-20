package com.sybd.security.oauth2.server.controller;

import com.sybd.security.oauth2.server.core.ApiResult;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@FrameworkEndpoint
public class RevokeTokenEndpoint {
    @Resource//(name = "tokenServices")
    ConsumerTokenServices tokenServices;

    @RequestMapping(method = RequestMethod.POST, value = "/tokens/revoke/{tokenId:.*}")
    @ResponseBody
    public ApiResult revokeToken(@PathVariable String tokenId) {
        tokenServices.revokeToken(tokenId);
        return ApiResult.success((Object)tokenId);
    }
}
