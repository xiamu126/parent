package com.sybd.znld.light;

import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrganizationMapper organizationMapper;

    @Test
    public void test(){
        var user = this.userMapper.selectById("a6b354d551f111e9804a0242ac110007");
        Assert.assertNotNull(user);
    }

    @Test
    public void test1(){
        var organ = this.organizationMapper.selectById("88cc4ad365d9493f85db160b336c8414");
        log.debug(organ.toString());
    }
}
