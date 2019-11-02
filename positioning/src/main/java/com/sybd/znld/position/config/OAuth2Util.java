package com.sybd.znld.position.config;

import com.jayway.jsonpath.JsonPath;
import com.sybd.znld.model.lamp.dto.CheckedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2Util {
    @Value("${auth-server}")
    private String oauth2Server;
    @Autowired
    private RestTemplate restTemplate;

    public boolean check(String token){
        var url = oauth2Server+"/oauth/check_token";
        var builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("token", token);
        var tmp = this.restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, Object.class);
        var body = tmp.getBody();
        if(body == null){
            return false;
        }
        String error = JsonPath.read(body, "$.error");
        return error == null;
    }

    public boolean delete(String token){
        var url = oauth2Server+"/oauth/token/revoke";
        var builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("token", token);
        var tmp = this.restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, null, Object.class);
        var body = tmp.getBody();
        if(body == null){
            return false;
        }
        int code = JsonPath.read(body, "$.code");
        return code == 0;
    }

    public boolean delete(String clientId, String username, String token){
        var url = oauth2Server+"/oauth/tokens/revoke";
        var builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("token", token);
        var headers = new HttpHeaders();
        headers.add("client_id", clientId);
        headers.add("username", username);
        var entity = new HttpEntity<>(null, headers);
        var tmp = this.restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, entity, Object.class);
        var body = tmp.getBody();
        if(body == null){
            return false;
        }
        int code = JsonPath.read(body, "$.code");
        return code == 0;
    }
}
