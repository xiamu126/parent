package com.sybd.znld.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.model.lamp.dto.RegionsAndDataStreams;
import com.sybd.znld.web.App;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class})
public class DeviceControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void test() throws Exception {
        var dataStream = "3303_0_5700";
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/avg/regions/dataStream/{dataStream}/{beginTimestamp}/{endTimestamp}",
                dataStream, beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var regionIds = List.of("62fa3afb56a111e98edc0242ac110007", "63096a3956a111e98edc0242ac110007");
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(regionIds)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void test2() throws Exception {
        var region = "神宇北斗测试区域1";
        var dataStreamIds = List.of("3303_0_5700", "3304_0_5700", "3325_0_5700", "3325_1_5700");
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/avg/region/{region}/dataStreams/{beginTimestamp}/{endTimestamp}",
                region, beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(dataStreamIds)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void test3() throws Exception {
        var region = "62fa3afb56a111e98edc0242ac110007";
        var dataStreams = List.of("温度", "湿度", "PM2.5", "PM10");
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/avg/region/{regionId}/dataStreams/{beginTimestamp}/{endTimestamp}",
                region, beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(dataStreams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void test4() throws Exception {
        var regions = List.of("62fa3afb56a111e98edc0242ac110007", "神宇北斗测试区域2");
        var dataStream = "温度";
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/avg/regions/dataStream/{dataStream}/{beginTimestamp}/{endTimestamp}",
                dataStream, beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(regions)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void test5() throws Exception {
        var regions = List.of("62fa3afb56a111e98edc0242ac110007", "神宇北斗测试区域2");
        var dataStreams = List.of("温度", "湿度", "PM2.5", "PM10");
        var regionsAndDataStreams = new RegionsAndDataStreams();
        regionsAndDataStreams.regions = regions;
        regionsAndDataStreams.dataStreams = dataStreams;
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/avg/regions/dataStreams/{beginTimestamp}/{endTimestamp}",
                beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(regionsAndDataStreams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void test6() throws Exception {
        var deviceIds = List.of(522756040, 522756075, 518452664, 42939715, 520914939);
        var dataStream = "温度";
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.of(2019,5,12,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/avg/deviceIds/dataStream/{dataStream}/{beginTimestamp}/{endTimestamp}",
                dataStream, beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(deviceIds)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }
}
