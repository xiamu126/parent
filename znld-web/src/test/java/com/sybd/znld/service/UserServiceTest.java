package com.sybd.znld.service;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.model.user.UserEntity;
import com.sybd.znld.model.user.dto.RegisterInput;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserService userService;
    @Autowired
    private ProjectConfig projectConfig;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        var seconds = projectConfig.getCacheOfNullExpirationTime().getSeconds();
        log.debug(Long.toString(seconds));
    }

    @After
    public void after(){
        log.debug("等待可能的定时任务执行完毕……");
        try {
            var tmp = this.userService.getExpirationTime().getSeconds();
            Thread.sleep(tmp*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        var user = new UserEntity();
        user.setId("0b494009f37711e88347000c294eb278");
        user.setName("yyy");
        user.setAge((short)17);
        var ret = this.userService.updateById(user);
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
        var user = this.userService.register(new RegisterInput("qqq","123456"));
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getName(), "qqq");
    }

    @Test
    public void verify(){
        var user = "xxx";
        var pwd = "123";
        var entity = this.userService.verify(user, pwd);
        log.debug(entity == null ? "null" : entity.toString());
        log.debug(this.getClass().getName());
    }

    @Test
    public void update(){
        var entity = new UserEntity();
        entity.setId("a48bfa0ff9e711e880a0000c294eb278");
        entity.setAge((short)18);
        var ret = this.userService.updateById(entity);
        log.debug(ret.toString());
    }
}
