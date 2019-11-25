package com.sybd.znld.web.service;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.model.lamp.LampModel;
import com.sybd.znld.model.rbac.dto.InitAccountInput;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.util.MD5;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.NoSuchAlgorithmException;
import java.util.List;

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

    @Autowired
    private UserMapper userMapper;

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
    public void getUserByIdByMapper(){
        var user = this.userMapper.selectById("a6a96ebc51f111e9804a0242ac110007");
        Assert.assertNotNull(user);
    }

    @Test
    public void getUserById(){
        var user = this.userService.getUserById("a6a96ebc51f111e9804a0242ac110007");
        Assert.assertNotNull(user);
    }
    @Test
    public void getUserByIdNull(){
        var user = this.userService.getUserById(null);
        Assert.assertNull(user);

    }

    @Test
    public void getUserByOrganId(){
        var list = this.userService.getUserByOrganizationId("a69ce5bf51f111e9804a0242ac110007");
        Assert.assertNotNull(list);
    }

    @Test
    public void getUserByName(){
        var user = this.userService.getUserByName("sybd_test_user");
        Assert.assertNotNull(user);
    }

    @Test
    public void updateById(){
        var user = new UserModel();
        user.id = "a6b354d551f111e9804a0242ac110007";
        var tmp = this.userService.modifyUserById(user);
        log.debug(tmp.toString());
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
        var ret = this.userService.modifyUserById(entity);
        log.debug(ret.toString());
    }

    // 新建账号
    @Test
    public void initAccount() throws NoSuchAlgorithmException {
        var user = new UserModel();
        user.name = "zhengzhou";
        user.password = MD5.encrypt(MD5.encrypt("2019").toLowerCase()).toLowerCase();
        var data = new InitAccountInput();
        data.user = user;
        data.oauth2ClientId = "sybd_znld_test";
        data.organizationName = "河南郑州";
        data.regionName = "郑州汉江路";
        var lamp1 = new LampModel();
        lamp1.apiKey = "fN8PGSJ3VoIOSoznGWuGeC25PGY=";
        lamp1.deviceId = 522756075;
        lamp1.imei = "868194030006128";
        lamp1.deviceName = "郑州汉江1";
        lamp1.longitude = "113.6492192021";
        lamp1.latitude = "34.7217506896";
        var lamp2 = new LampModel();
        lamp2.apiKey = "fN8PGSJ3VoIOSoznGWuGeC25PGY=";
        lamp2.deviceId = 522756040;
        lamp2.imei = "868194030003265";
        lamp2.deviceName = "郑州汉江2";
        lamp2.longitude = "113.6492192021";
        lamp2.latitude = "34.7217506896";
        data.lamps = List.of(lamp1, lamp2);

        this.userService.initAccount(data);
    }

    @Test
    public void test() throws NoSuchAlgorithmException {
        var encoder = new BCryptPasswordEncoder(10);
        var password = MD5.encrypt(MD5.encrypt("2019").toLowerCase()).toLowerCase();
        log.debug(encoder.encode(password));
        log.debug(encoder.encode(password));
        log.debug(encoder.encode(password));
    }
}
