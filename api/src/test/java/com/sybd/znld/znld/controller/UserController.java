package com.sybd.znld.znld.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.service.rbac.IUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
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
        MockHttpServletRequestBuilder action = MockMvcRequestBuilders.get("/api/user/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8);
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
}
