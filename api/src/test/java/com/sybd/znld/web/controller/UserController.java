package com.sybd.znld.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import com.sybd.znld.service.rbac.IUserService;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.NoSuchAlgorithmException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserController {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private IUserService userService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    private String salt = BCrypt.gensalt();
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void test(){
        var tmp = encoder.encode("defadfc2ad2fbb4fbcdc4d64f6c8d823");
        var ret = encoder.matches("defadfc2ad2fbb4fbcdc4d64f6c8d823", "$2a$10$EumDON8cvvcKVk5QwQwHm.q2WsUoCD43Y8W0uCzkoRCHeAXsDEOSK");
        log.debug(tmp);
        Assert.assertTrue(ret);
    }

    @Test
    public void getUserInfo(){
        String id = "0b494009f37711e88347000c294eb278";
        MockHttpServletRequestBuilder action = MockMvcRequestBuilders.get("/api/user/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8);
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
}
