package com.sybd.znld.znld.service.ministar;

import com.sybd.znld.model.ministar.TwinkleBeautyGroupModel;
import com.sybd.znld.service.ministar.IMiniStarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.internal.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MiniStarServiceTest {
    private final Logger log = LoggerFactory.getLogger(MiniStarServiceTest.class);

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private IMiniStarService miniStarService;

    @Test
    public void addTwinkleBeautyGroup(){
        var model = new TwinkleBeautyGroupModel();
        model.beginTime = LocalDateTime.now().plusDays(1);
        model.endTime = model.beginTime.plusHours(1);
        model.regionId = "46b743284baf11e993a60242ac110006";
        var ret = this.miniStarService.addTwinkleBeautyGroup(model);
        Assert.notNull(ret);
    }

    @Test
    public void test(){
    }

    @Test
    public void testMsg() throws IOException {
        var bytes = new ByteArrayOutputStream();
        var head = "DG".getBytes();
        bytes.write(head);
        var lamp_number = 0x3001; // 14位，最大0x3fff
        var lamp_status = 2;
        var number_status = (short)(lamp_number << 2 | lamp_status);
        var t1 = (byte)(number_status >>> 8);
        var t2 = (byte)(number_status & 0x00ff);
        bytes.write(t1);
        bytes.write(t2);
        var cmd_count = (byte)0;
        bytes.write(cmd_count);
        var item_count = (byte)1;
        var current_item = (byte)1;
        bytes.write(item_count);
        bytes.write(current_item);
        var color_info = 0;
        var color_count = 3;
        var color_info_count = (byte)(color_info >>> 5 | color_count << 3);
        bytes.write(color_info_count);
        var color_number = (byte)0;
        var color_r = (byte)255;
        var color_g = (byte)255;
        var color_b = (byte)255;
        bytes.write(color_number);
        bytes.write(color_r);
        bytes.write(color_g);
        bytes.write(color_b);
        color_number = (byte)1;
        bytes.write(color_number);
        bytes.write(color_r);
        bytes.write(color_g);
        bytes.write(color_b);
        color_number = (byte)2;
        bytes.write(color_number);
        bytes.write(color_r);
        bytes.write(color_g);
        bytes.write(color_b);
        var speed = (byte)100;
        bytes.write(speed);
        var begin_time = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        var end_time = LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli();
        bytes.write((int)begin_time);
        bytes.write((int)end_time);
        bytes.write("JW".getBytes());
        var tmp = bytes.toByteArray();
        System.out.println(lamp_number);
    }
}
