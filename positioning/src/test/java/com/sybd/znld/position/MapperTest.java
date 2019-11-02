package com.sybd.znld.position;

import com.sybd.znld.mapper.lamp.GpggaMapper;
import com.sybd.znld.model.lamp.GpggaModel;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
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

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MapperTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private GpggaMapper gpggaMapper;

    @Test
    public void test1(){
        var model = new GpggaModel();
        model.content = "test";
        var ret = this.gpggaMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test2(){
        var ret = this.gpggaMapper.append("25a01019ff6c4befb8ee98af529cce1a", "\r\ntest");
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test3(){
        var ret = this.gpggaMapper.selectBetween(MyDateTime.toLocalDateTime("2019-10-17 00:00:00"),MyDateTime.toLocalDateTime("2019-10-17 09:00:00"));
        Assert.assertFalse(ret.isEmpty());
    }

    @Test
    public void test4(){
        var ret = this.gpggaMapper.appendByFilename("filename123", "\r\ntest");
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test5(){
        var ret = this.gpggaMapper.selectByFilename("file_20191023085633_290.log");
        Assert.assertNotNull(ret);
    }
}
