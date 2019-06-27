package com.sybd.znld.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.model.lamp.dto.DeviceIdsAndDataStreams;
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


    //获取设备的某个资源历史数据，指定开始时间、结束时间
    @Test
    public void test() throws Exception {
        var deviceId = 522756040;
        var dataStreamId = "3303_0_5700";
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/history/pretty/{deviceId}/{dataStreamId}/{beginTimestamp}/{endTimestamp}",
                deviceId, dataStreamId, beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    //获取设备的某个资源历史数据，仅指定开始时间，结束时间为默认当前时间
    @Test
    public void test2() throws Exception {
        var deviceId = 522756040;
        var dataStreamId = "3303_0_5700";
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/history/pretty/{deviceId}/{dataStreamId}/{beginTimestamp}",
                deviceId, dataStreamId, beginTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    //获取多个区域的某时间段内的多个资源的各自的平均值
    @Test
    public void test3() throws Exception {
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").exists())
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    //获取某个区域的某时间段内的多个资源的各自的平均值

    @Test
    public void test11() throws Exception {
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
    public void test22() throws Exception {
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
    public void test33() throws Exception {
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
    public void test44() throws Exception {
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
    public void test66() throws Exception {
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

    @Test
    public void test77() throws Exception {
        var deviceId = 522756040;
        var dataStreams = List.of("温度", "湿度", "PM2.5", "PM10");
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.of(2019,5,12,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/avg/deviceId/{deviceId}/dataStreams/{beginTimestamp}/{endTimestamp}",
                deviceId, beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
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
    public void test88() throws Exception {
        var deviceIds = List.of(522756040, 522756075, 518452664, 42939715, 520914939);
        var dataStreams = List.of("温度", "湿度", "PM2.5", "PM10");
        var deviceIdsAndDataStreams = new DeviceIdsAndDataStreams();
        deviceIdsAndDataStreams.deviceIds = deviceIds;
        deviceIdsAndDataStreams.dataStreams = dataStreams;
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/avg/deviceIds/dataStreams/{beginTimestamp}/{endTimestamp}",
                beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(deviceIdsAndDataStreams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void test99() throws Exception {
        var deviceId =522756040;
        var dataStreams = List.of("温度", "湿度", "PM2.5", "PM10");
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/last/deviceId/{deviceId}/dataStreams",
                deviceId, beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
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
    public void test100() throws Exception {
        var deviceIds = List.of(522756040, 522756075, 518452664, 42939715, 520914939);
        var dataStream = "温度";
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/last/deviceIds/dataStream/{dataStream}",
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

    @Test
    public void test101() throws Exception {
        var deviceIds = List.of(522756040, 522756075, 518452664, 42939715, 520914939);
        var dataStreams = List.of("温度", "湿度", "PM2.5", "PM10");
        var deviceIdsAndDataStreams = new DeviceIdsAndDataStreams();
        deviceIdsAndDataStreams.deviceIds = deviceIds;
        deviceIdsAndDataStreams.dataStreams = dataStreams;
        var beginTimestamp = LocalDateTime.of(2018,1,1,0,0,1)
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var endTimestamp = LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/last/deviceIds/dataStreams",
                beginTimestamp, endTimestamp).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(deviceIdsAndDataStreams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }
}
