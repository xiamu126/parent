package com.sybd.znld.service.v2;

import com.sybd.znld.model.rbac.OrganizationModel;
import com.sybd.znld.service.rbac.OrganizationService;
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
public class OrganizationServiceTest {
    private final Logger log = LoggerFactory.getLogger(OrganizationServiceTest.class);
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Autowired
    private OrganizationService organizationService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void insertOrganization(){
        var model = new OrganizationModel();
        model.name = "测试组织名字";
        model.parentId = "";
        model.position = 1;
        model.status = 0;
        model.oauth2ClientId = "znld-public";
        var ret = this.organizationService.addOrganization(model);
        Assert.assertNotNull(ret);
        Assert.assertNotEquals("", ret.id);
    }

    @Test
    public void insertOrganization2(){
        var model = new OrganizationModel();
        model.name = "测试组织名字4";
        model.parentId = "b56e2648491e11e993a60242ac110006";
        model.position = 1;
        model.status = 0;
        model.oauth2ClientId = "znld-public";
        var ret = this.organizationService.addOrganization(model);
        Assert.assertNotNull(ret);
        Assert.assertNotEquals("", ret.id);
    }

    @Test
    public void selectById(){
        var ret = this.organizationService.getOrganizationById("b56e2648491e11e993a60242ac110006");
        Assert.assertNotNull(ret);
    }

    @Test
    public void deleteById(){
        var ret = this.organizationService.removeOrganizationById("230bbea1492811e993a60242ac110006");
        Assert.assertNotNull(ret);
        Assert.assertEquals("230bbea1492811e993a60242ac110006", ret.id);
    }

    @Test
    public void modifyById(){
        var model = new OrganizationModel();
        model.id = "b56e2648491e11e993a60242ac110006";
        model.name = "测试组织名字1";
        model.parentId = "";
        model.position = 0;
        model.status = 0;
        model.oauth2ClientId = "znld-public";
        var ret = this.organizationService.modifyOrganization(model);
        Assert.assertNotNull(ret);
        Assert.assertEquals("b56e2648491e11e993a60242ac110006", ret.id);
    }

    @Test
    public void selectByParentIdAndPosition(){
        var ret = this.organizationService.getOrganizationByParenIdAndPosition("", 0);
        Assert.assertNotNull(ret);
    }

    @Test
    public void selectByParentId(){
        var ret = this.organizationService.getOrganizationByParenId("b56e2648491e11e993a60242ac110006");
        Assert.assertNotNull(ret);
        Assert.assertTrue(!ret.isEmpty());
    }

    @Test
    public void removeOrganizationById(){
        var ret = this.organizationService.removeOrganizationById("b56e2648491e11e993a60242ac110006");
        Assert.assertNull(ret);
    }
}
