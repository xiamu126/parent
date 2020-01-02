package com.sybd.znld.oauth2.controller;

import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.oauth2.core.ApiResult;
import com.sybd.znld.oauth2.core.MyRedisTokenStore;
import com.sybd.znld.util.MyString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController // @FrameworkEndpoint比@Controller优先级低，如果存在相同的mapping，优先使用@Controller中定义的
public class RevokeTokenEndpoint {
    @Resource//(name = "tokenServices")
    private ConsumerTokenServices tokenServices;

    private final MyRedisTokenStore tokenStore;

    @Autowired
    public RevokeTokenEndpoint(MyRedisTokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @DeleteMapping(value = "/oauth/token/revoke", produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseApiResult revokeToken(HttpServletRequest request) {
        var token = request.getHeader("token");
        if(MyString.isEmptyOrNull(token)) {
            return BaseApiResult.fail();
        }
        if(this.tokenServices.revokeToken(token)) {
            return BaseApiResult.success();
        } else {
            return BaseApiResult.fail();
        }
    }

    @DeleteMapping(value = "/oauth/tokens/revoke", produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseApiResult revokeTokens(HttpServletRequest request){
        var clientId = request.getHeader("client_id");
        var userName = request.getHeader("username");
        var list = this.tokenStore.findTokensByClientIdAndUserName(clientId, userName);
        if(list != null && !list.isEmpty()){
            list.forEach(l -> {
                this.tokenStore.removeAccessToken(l);
                this.tokenStore.removeRefreshToken(l.getRefreshToken());
            });
            return BaseApiResult.success();
        }
        return BaseApiResult.fail();
    }

    @GetMapping(value = "/oauth/token/check/{token}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseApiResult checkToken(@PathVariable("token") String token) {
        var accessToken = this.tokenStore.readAccessToken(token);
        if(accessToken == null || accessToken.isExpired()) {
            return BaseApiResult.fail();
        }
        return BaseApiResult.success();
    }
}
