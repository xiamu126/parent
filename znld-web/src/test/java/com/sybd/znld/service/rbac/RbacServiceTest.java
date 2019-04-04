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
        model.url = "/";
        model.type = AuthorityModel.Type.Other;
        var ret = this.rbacService.addAuth(model);
        Assert.assertNotNull(ret);
    }

    @Test
    public void addRole(){
        var model = new RoleModel();
        model.name = "测试角色";
        model.type = RoleModel.Type.ANONYMOUS;
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
}
