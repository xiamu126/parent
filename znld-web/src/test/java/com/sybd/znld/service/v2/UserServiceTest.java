package com.sybd.znld.service.v2;

import com.sybd.rbac.model.UserModel;
import com.sybd.znld.service.rbac.UserService;
import com.sybd.znld.service.rbac.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    private final Logger log = LoggerFactory.getLogger(UserServiceTest.class);
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void insertUser(){
        var model = new UserModel();
        model.id = "1";
        model.name = "test3";
        model.password = "123456";
        model.gender = 1;
        model.organizationId = "";
        model.lastLoginIp = "";
        var user = this.userService.addUser(model);
        Assert.assertNotNull(user);
        Assert.assertNotEquals(user.id, "");
    }

    @Test
    public void getUser(){
        var user = this.userService.getUserById("3dfc0a90446d11e993a60242ac110006");
        Assert.assertNotNull(user);
    }

    @Test
    public void getUserByName(){
        var user = this.userService.getUserByName("test");
        Assert.assertNotNull(user);
    }

    @Test
    public void modifyUser(){
        var user = new UserModel();
        user.id = "8f9ced90446d11e993a60242ac110006";
        user.name = "test2";
        user.phone = "12345678900";
        var ret = this.userService.modifyUserById(user);
        Assert.assertNotNull(ret);
    }

    @Test
    public void getAuthPack(){
        var ret = this.userMapper.selectAuthPackByUserId("a6a96ebc51f111e9804a0242ac110007");
        Assert.assertNotNull(ret);
    }

    @Test
    public void addAuth(){
        /*var serverInclude = new RbacInfo.Rbac.Server.Include();
        serverInclude.methods = List.of("*");
        serverInclude.path = "*";
        var serverExclude = new RbacInfo.Rbac.Server.Exclude();
        serverExclude.methods = List.of("*");
        serverExclude.path = "/api/v1/device/execute/*";
        var htmlInclude = new RbacInfo.Rbac.Html.Include();
        var htmlExclude =  new RbacInfo.Rbac.Html.Exclude();
        htmlExclude.path = "/LandscapeLight";
        htmlExclude.selectors = List.of("*");

        var html = new RbacInfo.Rbac.Html();
        html.includes = List.of(htmlInclude);
        html.excludes = List.of(htmlExclude);
        var server = new RbacInfo.Rbac.Server();
        server.includes = List.of(serverInclude);
        server.excludes = List.of(serverExclude);
        var rbac = new RbacInfo.Rbac();
        rbac.html = html;
        rbac.server = server;
        var rbacInfo = new RbacInfo();
        rbacInfo.rbac = rbac;*/
    }
}
