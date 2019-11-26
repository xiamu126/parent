package com.sybd.znld.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.account.service.IUserService;
import com.sybd.znld.mapper.lamp.LocationMapper;
import com.sybd.znld.mapper.rbac.*;
import com.sybd.znld.model.lamp.LocationModel;
import com.sybd.znld.model.rbac.AuthorityGroupModel;
import com.sybd.znld.model.rbac.RoleModel;
import com.sybd.znld.model.rbac.dto.*;
import com.sybd.znld.util.MD5;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserController {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private IUserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    private String salt = BCrypt.gensalt();
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void getCaptcha() throws Exception {
        var action = MockMvcRequestBuilders.get("/api/v2/user/login/captcha").accept(MediaType.APPLICATION_JSON_UTF8);
        var ret = this.mockMvc.perform(action).andReturn().getRequest();
        log.debug(ret.toString());
    }

    @Test
    public void getUserInfo(){
        String id = "0b494009f37711e88347000c294eb278";
        var action = MockMvcRequestBuilders.get("/api/user/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String result = this.mockMvc.perform(action)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                    .andReturn().getResponse().getContentAsString();
            log.debug(result);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @Test
    public void getUserInfoFail(){
        String id = "123";
        var action = MockMvcRequestBuilders.get("/api/user/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String result = this.mockMvc.perform(action)
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
            log.debug(result);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @Test
    public void register() throws Exception {
        var pwd = MD5.encrypt("2019").toLowerCase();
        pwd = MD5.encrypt(pwd).toLowerCase();
        var registerInput = new RegisterInput("weihai",pwd, "099060a6971911e9b0790242c0a8b006");
        var action = MockMvcRequestBuilders.post("/api/v1/user/register").accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(registerInput)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void testPwd() throws NoSuchAlgorithmException {
        var md5 = MD5.encrypt("2019").toLowerCase();
        md5 = MD5.encrypt(md5).toLowerCase();
        log.debug(md5); // ff38998d16d23e78d7ec74c4ad327985
        var pwd = md5;
        var encoder = new BCryptPasswordEncoder(10);
        var epwd = encoder.encode(pwd);
        epwd = "$2a$10$MOkaye4NvkDqcnw5ye/MyOy3ijtstvyj69zCt3y.0EXXmSTuF8Mta";
        var ret = encoder.matches(pwd, epwd);
        Assert.assertTrue(ret);
    }

    @Autowired
    public AuthorityGroupMapper authorityGroupMapper;
    @Autowired
    public AuthorityMapper authorityMapper;

    // 新增权限组
    @Test
    public void addAuthGroup(){
        var model = new AuthorityGroupModel();
        model.name = "超级管理员权限组";
        model.organizationId = "a69ce5bf51f111e9804a0242ac110007";
        this.authorityGroupMapper.insert(model);
    }

    @Autowired
    public RoleMapper roleMapper;
    @Autowired
    public RoleAuthorityGroupMapper roleAuthorityGroupMapper;
    // 新增角色
    @Test
    public void addRole(){
        var model = new RoleModel();
        model.name = "超级管理员";
        model.organizationId = "a69ce5bf51f111e9804a0242ac110007";
        this.roleMapper.insert(model);
    }

    @Autowired
    public UserRoleMapper userRoleMapper;


    private void addDetail(List<RbacApiInfo.Detail> list, RbacApiInfo.Detail detail){
        for(var d : list){
            if(d.equals(detail)){ // path和methods完全相等
                return; // 不做任何修改，直接返回
            }else if(d.path.equals(detail.path) && !d.methods.equals(detail.methods)){ // 只有路径相等，methods不同
                d.methods.removeAll(detail.methods);
                d.methods.addAll(detail.methods); // 去重合并
                return; // 返回合并后的内容
            }
        }
        // 到这里意味着是全新的内容
        list.add(detail);
    }

    private void addDetails(List<RbacApiInfo.Detail> list1, List<RbacApiInfo.Detail> list2){
        if(list1 == null) return;
        if(list2 == null || list2.isEmpty()) return;
        for(var detail : list2){
            addDetail(list1, detail);
        }
    }

    @Test
    public void detailEqual(){
        RbacApiInfo.Detail detail1 = new RbacApiInfo.Detail();
        detail1.path = "/test";
        detail1.methods = List.of("test");
        RbacApiInfo.Detail detail2 = new RbacApiInfo.Detail();
        detail2.path = "/test";
        detail2.methods = new ArrayList<>();
        detail2.methods.add("test");
        Assert.assertEquals(detail1, detail2);
    }

    @Test
    public void detailListEqual(){
        List<RbacApiInfo.Detail> exclude1 = new ArrayList<>();
        RbacApiInfo.Detail detail1 = new RbacApiInfo.Detail();
        detail1.path = "/test";
        detail1.methods = List.of("test");
        exclude1.add(detail1);

        List<RbacApiInfo.Detail> exclude2 = new ArrayList<>();
        RbacApiInfo.Detail detail2 = new RbacApiInfo.Detail();
        detail2.path = "/test";
        detail2.methods = new ArrayList<>();
        detail2.methods.add("test");
        exclude2.add(detail2);

        Assert.assertEquals(exclude1, exclude2);

        exclude1.removeAll(exclude2);

        log.debug(String.valueOf(exclude1.size()));
    }

    @Test
    public void addDetails(){
        List<RbacApiInfo.Detail> exclude1 = new ArrayList<>();
        RbacApiInfo.Detail detail1 = new RbacApiInfo.Detail();
        detail1.path = "/path";
        detail1.methods = new ArrayList<>();
        detail1.methods.add("method");
        exclude1.add(detail1);

        List<RbacApiInfo.Detail> exclude2 = new ArrayList<>();
        RbacApiInfo.Detail detail2 = new RbacApiInfo.Detail();
        detail2.path = "/path2";
        detail2.methods = new ArrayList<>();
        detail2.methods.add("method");
        exclude2.add(detail2);

        this.addDetails(exclude1, exclude2);

        log.debug(String.valueOf(exclude1.size()));
    }

    @Autowired
    private LocationMapper locationMapper;

    @Test
    public void addRoot(){
        var root = new LocationModel();
        root.name = "某个根节点";
        root.level = 1;
        root.sequenceNumber = 1;
        root.rootId = root.id;
        root.organizationId = "a69ce5bf51f111e9804a0242ac110007";
        var ret = this.locationMapper.insert(root);
        Assert.assertTrue(ret > 0);
    }
    // 新增路径树
    @Test
    public void addNode(){
        var node = new LocationModel();
        node.name = "某个子节点";
        node.level = 2;
        node.sequenceNumber = 2;
        node.rootId = "d5b725a493054249860dbe25c0f6cdda";
        node.organizationId = "a69ce5bf51f111e9804a0242ac110007";
        var ret = this.locationMapper.insert(node);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void addNode2(){
        var node = new LocationModel();
        node.name = "松陵";
        node.level = 3;
        node.sequenceNumber = 3;
        node.rootId = "aae19a2a9c8245dbb7c99432d5614d02";
        node.organizationId = "a69ce5bf51f111e9804a0242ac110007";
        var ret = this.locationMapper.insert(node);
        Assert.assertTrue(ret > 0);
        node = new LocationModel();
        node.name = "同里";
        node.level = 3;
        node.sequenceNumber = 4;
        node.rootId = "aae19a2a9c8245dbb7c99432d5614d02";
        node.organizationId = "a69ce5bf51f111e9804a0242ac110007";
        ret = this.locationMapper.insert(node);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void addChild(){
        var parent = this.locationMapper.selectByOrganIdName("a69ce5bf51f111e9804a0242ac110007", "苏州");
        Assert.assertNotNull(parent);

    }

    @Test
    public void getChild(){
        var root = this.locationMapper.selectRoot("a69ce5bf51f111e9804a0242ac110007", "苏州");
        Assert.assertNotNull(root);
        var children = this.locationMapper.selectChild("aae19a2a9c8245dbb7c99432d5614d02");
        Assert.assertTrue(children.size() > 0);
        children = this.locationMapper.selectChildren("aae19a2a9c8245dbb7c99432d5614d02");
        Assert.assertTrue(children.size() > 0);
    }
}
