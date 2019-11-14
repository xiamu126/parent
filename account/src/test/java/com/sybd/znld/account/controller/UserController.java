package com.sybd.znld.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.sybd.znld.account.service.IUserService;
import com.sybd.znld.mapper.rbac.*;
import com.sybd.znld.model.rbac.AuthorityGroupModel;
import com.sybd.znld.model.rbac.AuthorityModel;
import com.sybd.znld.model.rbac.RoleAuthorityGroupModel;
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

import java.io.IOException;
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
    // 新增api操作
    @Test
    public void addAuth() throws IOException {
        var app = "znld"; // 这个操作是针对哪个app的
        var api = new RbacApiInfo(app); // 针对api类型的操作（权限）集合
        var includeDetail = new RbacApiInfo.Detail();
        includeDetail.path = "/api/v1/device/*";
        includeDetail.methods = List.of("post");
        var excludeDetail = new RbacApiInfo.Detail();
        excludeDetail.path = "/api/v1/ministar/*";
        excludeDetail.methods = List.of("*");
        api.include = List.of(includeDetail);
        api.exclude = List.of(excludeDetail);
        log.debug(api.toJson());
        var model = new AuthorityModel();
        model.authorityGroupId = "260fd9fbd1af4d4c9bb4b61e0957dadc";
        model.name = "测试api操作";
        model.uri = api.toJson();
        this.authorityMapper.insert(model);
    }

    @Autowired
    public RoleMapper roleMapper;
    @Autowired
    public RoleAuthGroupMapper roleAuthGroupMapper;
    // 新增角色
    @Test
    public void addRole(){
        var model = new RoleModel();
        model.name = "超级管理员";
        model.organizationId = "a69ce5bf51f111e9804a0242ac110007";
        this.roleMapper.insert(model);
    }

    // 关联角色和权限组
    @Test
    public void addRoleAuth(){
        var model = new RoleAuthorityGroupModel();
        model.roleId = "28c98dcc82fe4bb1bedd0bec84a8a8f1";
        model.authGroupId = "c070a90bede849f6b937260aee1f65b6";
        this.roleAuthGroupMapper.insert(model);
    }

    @Autowired
    public UserRoleMapper userRoleMapper;
    // 根据用户id获取权限相关信息
    @Test
    public void getRbacInfoByUserId() {
        var userId = "a6b354d551f111e9804a0242ac110007";
        var userRoleModel = this.userRoleMapper.selectByUserId(userId);
        // 根据角色获取权限
        var roleAuthModel = this.roleAuthGroupMapper.selectByRoleId(userRoleModel.get(0).roleId);
        var authorities = this.authorityMapper.selectByAuthGroupId(roleAuthModel.get(0).authGroupId);
        Assert.assertTrue(authorities.size() > 0);
        authorities.forEach(a -> {
            String type = JsonPath.read(a.uri,"type");
            if(type.equals("api")){
                List<RbacApiInfo.Detail> exclude = JsonPath.read(a.uri,"exclude");
                List<RbacApiInfo.Detail> include = JsonPath.read(a.uri,"include");
                var summary = new RbacApiInfoSummary();
                summary.exclude = exclude;
                summary.include = include;
                log.debug(summary.toJson());
            }else if(type.equals("web")){
                List<RbacWebInfo.Detail> exclude = null;
                try {
                    exclude = JsonPath.read(a.uri,"exclude");
                }catch (PathNotFoundException ignored){ }
                List<RbacWebInfo.Detail> include = null;
                try {
                    include = JsonPath.read(a.uri,"include");
                }catch (PathNotFoundException ignored){ }
                var summary = new RbacWebInfoSummary();
                summary.exclude = exclude;
                summary.include = include;
                log.debug(summary.toJson());
            }
        });
    }

    // 新增web操作
    @Test
    public void addWebAuth() throws IOException {
        var app = "znld"; // 这个操作是针对哪个app的
        var web = new RbacWebInfo(app); // 针对web类型的操作（权限）集合
        var includeDetail = new RbacWebInfo.Detail();
        includeDetail.path = "/api/v1/device/*";
        includeDetail.selectors = List.of("selectorId");
        var excludeDetail = new RbacWebInfo.Detail();
        excludeDetail.path = "/api/v1/ministar/*";
        excludeDetail.selectors = List.of("*");
        web.include = List.of(includeDetail);
        web.exclude = List.of(excludeDetail);
        log.debug(web.toJson());
        var model = new AuthorityModel();
        model.authorityGroupId = "260fd9fbd1af4d4c9bb4b61e0957dadc";
        model.name = "测试web操作";
        model.uri = web.toJson();
        this.authorityMapper.insert(model);
    }

    // 新增超级管理员权限
    @Test
    public void addRootAuth() throws IOException {
        var app = "znld"; // 这个操作是针对哪个app的
        var web = new RbacWebInfo(app); // 针对web类型的操作（权限）集合
        var includeWebDetail = new RbacWebInfo.Detail();
        includeWebDetail.path = "/**";
        includeWebDetail.selectors = List.of("*");
        web.include = List.of(includeWebDetail);
        var model = new AuthorityModel();
        model.authorityGroupId = "c070a90bede849f6b937260aee1f65b6";
        model.name = "超级管理员web操作";
        model.uri = web.toJson();
        this.authorityMapper.insert(model);

        var api = new RbacApiInfo(app); // 针对api类型的操作（权限）集合
        var includeApiDetail = new RbacApiInfo.Detail();
        includeApiDetail.path = "/**";
        includeApiDetail.methods = List.of("*");
        api.include = List.of(includeApiDetail);
        log.debug(api.toJson());
        model = new AuthorityModel();
        model.authorityGroupId = "c070a90bede849f6b937260aee1f65b6";
        model.name = "超级管理员api操作";
        model.uri = api.toJson();
        this.authorityMapper.insert(model);
    }

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
    public void appendAuth() throws JsonProcessingException {
        var authGroupId = "a6bf84cc51f111e9804a0242ac110007";
        var authName = "测试权限2";
        var rbacApiInfo = new RbacApiInfo("znld");
        var excludeDetail = new RbacApiInfo.Detail();
        excludeDetail.path = "/api/v1/device/*";
        excludeDetail.methods.add("get");
        rbacApiInfo.exclude.add(excludeDetail);
        //
        var authority = this.authorityMapper.selectByAuthGroupIdAndAuthName(authGroupId, authName);
        if(authority != null){
            log.debug("这个权限组下已经存在名为["+authName+"]的权限");
            var objectMapper = new ObjectMapper();
            var rbacApiInfoTmp = objectMapper.readValue(authority.uri, RbacApiInfo.class);
            if(rbacApiInfoTmp.app.equals(rbacApiInfo.app) && rbacApiInfoTmp.type.equals(rbacApiInfo.type)){
                // 已经存在这个app及其type的权限信息，那么就追加
                if(rbacApiInfoTmp.exclude != null) {
                    // 合并相同路径的内容或添加
                    addDetails(rbacApiInfo.exclude, rbacApiInfoTmp.exclude);
                }

                authority.uri = rbacApiInfo.toJson();
                this.authorityMapper.update(authority);
            }
        }else {
            // 新增
            var model = new AuthorityModel();
            model.name = authName;
            model.authorityGroupId = authGroupId;
            model.uri = rbacApiInfo.toJson();
            this.authorityMapper.insert(model);
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
}
