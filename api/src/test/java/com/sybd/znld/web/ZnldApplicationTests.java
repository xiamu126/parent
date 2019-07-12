package com.sybd.znld.web;

import com.sybd.znld.model.onenet.OneNetKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ZnldApplicationTests {

    @Resource(name = "sslRestTemplate")
    private RestTemplate sslRestTemplate;

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    @Test
    public void test1() {
        var headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("appId", "P62Kv4O34of9Ny1MH0ASd_ZiIJka");
        requestBody.add("secret", "iZMBTyHCJE3RS_r2QvRqu_oF5MEa");
        var httpEntity = new HttpEntity<>(requestBody, headers);
        var ret = sslRestTemplate.exchange("https://180.101.147.89:8743/iocm/app/sec/v1.1.0/login", HttpMethod.POST, httpEntity, Object.class);
        log.debug(ret.toString());
    }

    @Test
    public void test2() {
        var headers = new HttpHeaders();
        headers.setContentType( MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("appId", "P62Kv4O34of9Ny1MH0ASd_ZiIJka");
        requestBody.add("secret", "iZMBTyHCJE3RS_r2QvRqu_oF5MEa");
        var httpEntity = new HttpEntity<>(requestBody, headers);
        var ret = restTemplate.exchange("https://180.101.147.89:8743/iocm/app/sec/v1.1.0/login", HttpMethod.POST, httpEntity, Object.class);
        log.debug(ret.toString());
    }
}
