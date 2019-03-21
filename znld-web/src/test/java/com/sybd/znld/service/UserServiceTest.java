package com.sybd.znld.service;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.model.user.UserEntity;
import com.sybd.znld.service.model.user.dto.RegisterInput;
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
public class UserServiceTest {
    private final Logger log = LoggerFactory.getLogger(UserServiceTest.class);
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserServiceI userService;
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
        try {
            long tmp = this.userService.getExpirationTime().getSeconds();
            Thread.sleep(tmp*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUserById(){
        UserEntity user = this.userService.getUserById("0b494009f37711e88347000c294eb278");
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getName(), "yyy");
        Assert.assertEquals(17, (short) user.getAge());
    }
    @Test
    public void getUserByIdNull(){
        UserEntity user = this.userService.getUserById(null);
        Assert.assertNull(user);

    }

    @Test
    public void getUserByName(){
        UserEntity user = this.userService.getUserByName("yyy");
        Assert.assertNotNull(user);
        Assert.assertEquals(17, (short) user.getAge());
    }

    @Test
    public void updateById(){
        UserEntity user = new UserEntity();
        user.setId("0b494009f37711e88347000c294eb278");
        user.setName("yyy");
        user.setAge((short)17);
        UserEntity ret = this.userService.updateById(user);
        Assert.assertNotNull(ret);
        Assert.assertEquals(17, (short) ret.getAge());
    }

    @Test
    public void getUserByIdFail(){
        UserEntity user = this.userService.getUserById("0b494009f37711e88347000c294eb279");
        log.debug(user == null ? "null" : user.toString());
        Assert.assertNull(user);
    }

    @Test
    public void getUserByIdFail2(){
        UserEntity user = this.userService.getUserById("0b494009f37711e88347000c294eb279");
        log.debug(user == null ? "null" : user.toString());
        Assert.assertNull(user);
    }

    @Test
    public void register(){
        UserEntity user = this.userService.register(new RegisterInput("qqq","123456"));
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getName(), "qqq");
    }

    @Test
    public void verify(){
        String user = "xxx";
        String pwd = "123";
        UserEntity entity = this.userService.verify(user, pwd);
        log.debug(entity == null ? "null" : entity.toString());
        log.debug(this.getClass().getName());
    }

    @Test
    public void update(){
        UserEntity entity = new UserEntity();
        entity.setId("a48bfa0ff9e711e880a0000c294eb278");
        entity.setAge((short)18);
        UserEntity ret = this.userService.updateById(entity);
        log.debug(ret.toString());
    }
}
