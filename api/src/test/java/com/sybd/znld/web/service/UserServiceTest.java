package com.sybd.znld.web.service;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.service.rbac.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
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
@Slf4j
public class UserServiceTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private IUserService userService;
    @Autowired
    private ProjectConfig projectConfig;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        long seconds = projectConfig.getCacheOfNullExpirationTime().getSeconds();
        log.debug(Long.toString(seconds));
    }

    @After
    public void after(){
        log.debug("等待可能的定时任务执行完毕……");
    }

    @Test
    public void getUserById(){
        var user = this.userService.getUserById("0b494009f37711e88347000c294eb278");
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getName(), "yyy");
        Assert.assertEquals(17, (short) user.getAge());
    }
    @Test
    public void getUserByIdNull(){
        var user = this.userService.getUserById(null);
        Assert.assertNull(user);

    }

    @Test
    public void getUserByName(){
        var user = this.userService.getUserByName("yyy");
        Assert.assertNotNull(user);
        Assert.assertEquals(17, (short) user.getAge());
    }

    @Test
    public void updateById(){
        var user = new UserModel();
        user.setId("0b494009f37711e88347000c294eb278");
        user.setName("yyy");
        user.setAge((short)17);
        var ret = this.userService.modifyUserById(user);
        Assert.assertNotNull(ret);
        Assert.assertEquals(17, (short) ret.getAge());
    }

    @Test
    public void getUserByIdFail(){
        var user = this.userService.getUserById("0b494009f37711e88347000c294eb279");
        log.debug(user == null ? "null" : user.toString());
        Assert.assertNull(user);
    }

    @Test
    public void getUserByIdFail2(){
        var user = this.userService.getUserById("0b494009f37711e88347000c294eb279");
        log.debug(user == null ? "null" : user.toString());
        Assert.assertNull(user);
    }

    @Test
    public void register(){
        var user = this.userService.register(new RegisterInput("weihai","2019", "099060a6971911e9b0790242c0a8b006"));
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getName(), "weihai");
    }

    @Test
    public void verify(){
        String user = "xxx";
        String pwd = "123";
        var entity = this.userService.verify(user, pwd);
        log.debug(entity == null ? "null" : entity.toString());
        log.debug(this.getClass().getName());
    }

    @Test
    public void update(){
        var entity = new UserModel();
        entity.setId("a48bfa0ff9e711e880a0000c294eb278");
        entity.setAge((short)18);
        var ret = this.userService.modifyUserById(entity);
        log.debug(ret.toString());
    }
}
