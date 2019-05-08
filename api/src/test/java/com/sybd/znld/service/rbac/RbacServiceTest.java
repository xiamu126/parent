package com.sybd.znld.service.rbac;

import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.model.rbac.*;
import com.sybd.znld.model.rbac.dto.RbacHtmlInfo;
import com.sybd.znld.model.rbac.dto.RbacApiInfo;
import com.sybd.znld.service.rbac.IRbacService;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RbacServiceTest {
    private final Logger log = LoggerFactory.getLogger(RbacServiceTest.class);
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private IRbacService rbacService;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void addOrgan(){
        var model = new OrganizationModel();
        model.name = "神宇北斗";
        model.oauth2ClientId = "sybd_znld_test";
        var ret = this.rbacService.addOrganization(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void removeOrgan(){
        var ret = this.rbacService.removeOrganizationByName("神宇北斗测试");
        Assert.assertTrue(ret.isSuccess());
    }

    @Test
    public void addAuthGroup(){
        var model = new AuthGroupModel();
        model.name = "测试权限组";
        model.parentId = "e22eb96b4f8f11e9804a0242ac110007";
        model.position = 0;
        var ret = this.rbacService.addAuthGroup(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void addAuth(){
        var model = new AuthorityModel();
        model.name = "测试权限";
        model.authorityGroupId = "bfb302934f8f11e9804a0242ac1100071";
        model.uri = "/";
        var ret = this.rbacService.addAuth(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void addRole(){
        var model = new RoleModel();
        model.name = "USER";
        model.organizationId = "a69ce5bf51f111e9804a0242ac110007";
        var ret = this.rbacService.addRole(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void addUserRole(){
        var model = new UserRoleModel();
        model.roleId = "3c0a4d524f9611e9804a0242ac110007";
        model.userId = "3dfc0a90446d11e993a60242ac110006";
        var ret = this.rbacService.addUserRole(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void addRoleAuth(){
        var model = new RoleAuthModel();
        model.roleId = "3c0a4d524f9611e9804a0242ac110007";
        model.authId = "dd468b264f9211e9804a0242ac110007";
        var ret = this.rbacService.addRoleAuth(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void getAuthList(){
        var ret = this.rbacService.getAuthoritiesByUserId("3dfc0a90446d11e993a60242ac110006");
        Assert.assertNotNull(ret);
    }

    @Test
    public void testRbacInfo(){
        var rbacHtmlInfo = new RbacHtmlInfo.Builder()
                .setApp("znld").setPath("/LandscapeLight")
                .setSelectors("div[class*='selectDateTimeLine'] > button[class*='sendBtn'] > span", "div[class*='tableBox2'] table td button:not([disabled]) > span")
                .build();
        log.debug(rbacHtmlInfo.getJsonString());
        log.debug(RbacHtmlInfo.getType(rbacHtmlInfo.getJsonString()));

        var rbacServerInfo = new RbacApiInfo.Builder()
                .setApp("znld").setPath("/api/v1/device/execute/*")
                .setMethods("POST")
                .build();
        log.debug(rbacServerInfo.getJsonString());
        log.debug(RbacApiInfo.getType(rbacServerInfo.getJsonString()));

    }

    @Test
    public void addHtmlAuth(){
        var rbacHtmlInfo = new RbacHtmlInfo.Builder()
                .setApp("znld").setPath("/LandscapeLight")
                .setSelectors("div[class*='selectDateTimeLine'] > button[class*='sendBtn'] > span", "div[class*='tableBox2'] table td button:not([disabled]) > span")
                .build();
        var ret = this.rbacService.addHtmlAuth("a6bf84cc51f111e9804a0242ac110007", rbacHtmlInfo, "测试权限1");
    }

    @Test
    public void addApiAuth(){
        var rbacApiInfo = new RbacApiInfo.Builder().setApp("znld").setPath("/api/v1/device/*").setMethods("GET").build();
        var ret = this.rbacService.addApiAuth("a6bf84cc51f111e9804a0242ac110007", rbacApiInfo, "测试权限2");
        Assert.assertNotNull(ret);
    }

    @Test
    public void addAuthToUser(){
        var authId = "56da794d5bf811e98edc0242ac110007";
        var roleId = "a70a253951f111e9804a0242ac110007";
        var ret = this.rbacService.addAuthToRole(authId, roleId);
        Assert.assertNotNull(ret);
    }

    @Test
    public void getRoles(){
        var tmp = this.userMapper.selectRolesByUserId("a6a96ebc51f111e9804a0242ac110007");
        Assert.assertNotNull(tmp);
        Assert.assertFalse(tmp.isEmpty());
    }

    @Test
    public void pathMatch(){
        var matcher = new AntPathMatcher();
        Assert.assertFalse(matcher.match("/api/v1/user/**", "/api/v1/device/info"));
        Assert.assertTrue(matcher.match("/api/v1/user/**", "/api/v1/user/a"));
        Assert.assertTrue(matcher.match("/api/v1/user/*", "/api/v1/user/a?p=0"));
        Assert.assertTrue(matcher.match("/api/v1/user/*", "/api/v1/user/"));
        Assert.assertFalse(matcher.match("/api/v1/user/*", "/api/v1/user"));
        Assert.assertTrue(matcher.match("/api/v1/user/**", "/api/v1/user/a/b?p=1"));
        Assert.assertTrue(matcher.match("/api/v1/user/?", "/api/v1/user/a"));
        Assert.assertFalse(matcher.match("/api/v1/user/?", "/api/v1/user/ab"));
    }

    @Test
    public void test(){
        var ret = this.userMapper.selectAuthPackByUserId("a6a96ebc51f111e9804a0242ac110007");
        log.debug(ret.toString());
    }
}
