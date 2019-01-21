package com.sybd.znld.service;

import com.sybd.znld.service.VideoConfigService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoConfigServiceTest {
    @Autowired
    private VideoConfigService videoConfigService;

    @Test
    public void test(){
        var tmp = videoConfigService.getConfigByCameraId("2");
        System.out.println(this.getClass().getName());
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
    }
}