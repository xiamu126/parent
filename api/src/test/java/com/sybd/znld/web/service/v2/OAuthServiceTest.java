package com.sybd.znld.web.service.v2;

import com.sybd.znld.model.oauth.OAuthClientDetailsModel;
import com.sybd.znld.service.oauth.IOAuthService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OAuthServiceTest {
    private final Logger log = LoggerFactory.getLogger(OAuthServiceTest.class);
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private IOAuthService oAuthService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void getClientDetails(){
        var list = this.oAuthService.getClientDetails();
        Assert.assertNotNull(list);
    }

    @Test
    public void getClientDetailsById(){
        var ret = this.oAuthService.getClientDetailsByClientId("znld");
        Assert.assertNotNull(ret);
    }

    @Test
    public void insertClientDetails(){
        var model = new OAuthClientDetailsModel();
        model.clientId = "test1";
        model.resourceIds = "znld-web";
        model.clientSecret = "E75F5A44F80B2E19D7828ED6F9D7C8AF";
        model.scope = "read,write,execute";
        model.authorizedGrantTypes = "client_credentials,password,authorization_code,refresh_token,implicit";
        model.webServerRedirectUri = "";
        model.authorities = "user";
        model.accessTokenValidity = 10;
        model.refreshTokenValidity = 100;
        model.additionalInformation = "";
        model.autoapprove = "false";
        var ret = this.oAuthService.insertClientDetails(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void updateClientDetailsByClientId(){
        var model = new OAuthClientDetailsModel();
        model.clientId = "test";
        model.resourceIds = "znld-web";
        model.clientSecret = "E75F5A44F80B2E19D7828ED6F9D7C8AF";
        model.scope = "read,write,execute";
        model.authorizedGrantTypes = "client_credentials,password,authorization_code,refresh_token,implicit";
        model.webServerRedirectUri = "";
        model.authorities = "user";
        model.accessTokenValidity = 10;
        model.refreshTokenValidity = 100;
        model.additionalInformation = "";
        model.autoapprove = "false";
        var ret = this.oAuthService.updateClientDetailsByClientId(model);
        Assert.assertTrue(ret);
    }

    @Test
    public void deleteClientDetailsByClientId(){
        var ret = this.oAuthService.deleteClientDetailsByClientId("test");
        Assert.assertTrue(ret);
    }
}
