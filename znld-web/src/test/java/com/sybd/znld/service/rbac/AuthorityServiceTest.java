package com.sybd.znld.service.rbac;

import com.sybd.znld.model.rbac.*;
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
public class AuthorityServiceTest {
    private final Logger log = LoggerFactory.getLogger(AuthorityServiceTest.class);
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private IAuthorityService authorityService;

    @Test
    public void addAuthGroup(){
        var model = new AuthGroupModel();
        model.name = "测试权限组";
        model.parentId = "e22eb96b4f8f11e9804a0242ac110007";
        model.position = 0;
        var ret = this.authorityService.addAuthGroup(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void addAuth(){
        var model = new AuthorityModel();
        model.name = "测试权限";
        model.authorityGroupId = "bfb302934f8f11e9804a0242ac1100071";
        model.url = "/";
        model.type = AuthorityModel.Type.Other;
        var ret = this.authorityService.addAuth(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void addRole(){
        var model = new RoleModel();
        model.name = "测试角色";
        model.type = RoleModel.Type.ANONYMOUS;
        var ret = this.authorityService.addRole(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void addUserRole(){
        var model = new UserRoleModel();
        model.roleId = "3c0a4d524f9611e9804a0242ac110007";
        model.userId = "3dfc0a90446d11e993a60242ac110006";
        var ret = this.authorityService.addUserRole(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void addRoleAuth(){
        var model = new RoleAuthModel();
        model.roleId = "3c0a4d524f9611e9804a0242ac110007";
        model.authId = "dd468b264f9211e9804a0242ac110007";
        var ret = this.authorityService.addRoleAuth(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void getAuthList(){
        var ret = this.authorityService.getAuthoritiesByUserId("3dfc0a90446d11e993a60242ac110006");
        Assert.assertNotNull(ret);
    }
}
