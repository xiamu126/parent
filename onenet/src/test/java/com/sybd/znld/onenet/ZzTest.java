package com.sybd.znld.onenet;

import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.model.onenet.DataPushModel;
import com.sybd.znld.onenet.controller.DataPushController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ZzTest {
    @Autowired
    private DataPushController dataPushController;
}
