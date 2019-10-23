package com.sybd.znld.oauth2.controller;

import com.sybd.znld.oauth2.core.ApiResult;
import com.sybd.znld.oauth2.core.MyRedisTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@FrameworkEndpoint // 比@Controller优先级低，如果存在相同的mapping，优先使用@Controller中定义的
public class RevokeTokenEndpoint {
    @Resource//(name = "tokenServices")
    private ConsumerTokenServices tokenServices;

    @Autowired
    private MyRedisTokenStore tokenStore;

    @RequestMapping(method = RequestMethod.DELETE, value = "/oauth/token/revoke")
    @ResponseBody
    public ApiResult revokeToken(HttpServletRequest request) {
        var authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
            var tokenId = authorization.substring("Bearer".length() + 1);
            tokenServices.revokeToken(tokenId);
            return ApiResult.success((Object) tokenId);
        }
        return ApiResult.fail("fail");
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/oauth/tokens/revoke")
    @ResponseBody
    public ApiResult revokeTokens(HttpServletRequest request){
        var clientId = request.getHeader("client_id");
        var userName = request.getHeader("username");
        var list = this.tokenStore.findTokensByClientIdAndUserName(clientId, userName);
        if(list != null && !list.isEmpty()){
            list.forEach(l -> {
                this.tokenStore.removeAccessToken(l);
                this.tokenStore.removeRefreshToken(l.getRefreshToken());
            });
            return ApiResult.success(list.size());
        }
        return ApiResult.fail();
    }
}
