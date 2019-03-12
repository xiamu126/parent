package com.sybd.znld.service.v2;

import com.sybd.znld.model.v2.rbac.UserModel;
import com.sybd.znld.service.v2.rbac.UserService;
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

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void insertUser(){
        var model = new UserModel();
        model.id = "1";
        model.name = "test";
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
        var ret = this.userService.modifyUser(user);
        Assert.assertNotNull(ret);
    }
}
