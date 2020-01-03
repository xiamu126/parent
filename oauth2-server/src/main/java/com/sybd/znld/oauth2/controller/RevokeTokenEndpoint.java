package com.sybd.znld.oauth2.controller;

import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController // @FrameworkEndpoint比@Controller优先级低，如果存在相同的mapping，优先使用@Controller中定义的
public class RevokeTokenEndpoint {
    private final DefaultTokenServices tokenServices;
    private final TokenStore tokenStore;
    private final RestTemplate restTemplate;

    @Autowired
    public RevokeTokenEndpoint(@Qualifier("JwtTokenStore") TokenStore tokenStore,
                               DefaultTokenServices tokenServices,
                               RestTemplate restTemplate) {
        this.tokenStore = tokenStore;
        this.tokenServices = tokenServices;
        this.restTemplate = restTemplate;
    }

    @DeleteMapping(value = "/oauth/token/revoke", produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseApiResult revokeToken(HttpServletRequest request) {
        var token = request.getHeader("token");
        if(MyString.isEmptyOrNull(token)) {
            log.error("参数错误");
            return BaseApiResult.fail();
        }
        if(this.tokenServices.revokeToken(token)) {
            return BaseApiResult.success();
        } else {
            log.error("删除失败");
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
