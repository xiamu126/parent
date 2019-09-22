package com.sybd.znld.onenet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.model.lamp.dto.DeviceIdsAndDataStreams;
import com.sybd.znld.model.lamp.dto.RegionsAndDataStreams;
import com.sybd.znld.model.ministar.dto.Subtitle;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.onenet.App;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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
    public void getPrettyHistoryData() throws Exception {
        var deviceId = 528130535;
        var dataStreamId = "3303_0_5700";
        var beginTimestamp = LocalDateTime.of(2019,7,11,0,0,1)
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
    public void getPrettyHistoryDataWithoutEndTimestamp() throws Exception {
        var deviceId = 528130535;
        var dataStreamId = "3303_0_5700";
        var beginTimestamp = LocalDateTime.of(2019,7,10,0,0,1)
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
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/avg/regionName/{regionName}/dataStreams/{beginTimestamp}/{endTimestamp}",
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
        var action = MockMvcRequestBuilders.get("/api/v1/device/data/avg/regionName/{regionId}/dataStreams/{beginTimestamp}/{endTimestamp}",
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
        var action = MockMvcRequestBuilders.post("/api/v1/device/data/last/deviceId/{deviceId}/dataStreams",
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

    @Test
    public void pushByDeviceIdOfDataStream() throws Exception {
        var deviceId = 528130535;
        var dataStream = "探针开关";
        var value = 1;
        var action = MockMvcRequestBuilders.put("/api/v1/device/status/deviceId/{deviceId}/dataStream/{dataStream}/value/{value}",
                deviceId, dataStream, value).accept(MediaType.APPLICATION_JSON_UTF8);
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void pullByDeviceIdOfDataStream() throws Exception {
        var deviceId = 528130535;
        var dataStream = "3342_6_5700";
        var action = MockMvcRequestBuilders.get("/api/v1/device/status/deviceId/{deviceId}/dataStream/{dataStream}",
                deviceId, dataStream).accept(MediaType.APPLICATION_JSON_UTF8);
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void pushByDeviceIdOfDataStreams() throws Exception {
        var deviceId = 528130535;
        var dataStreams = List.of("探针开关", "3342_5_5700");
        var value = 1;
        var action = MockMvcRequestBuilders.put("/api/v1/device/status/deviceId/{deviceId}/dataStreams/value/{value}",
                deviceId, value).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(dataStreams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void pullByDeviceIdOfDataStreams() throws Exception {
        var deviceId = 528130535;
        var dataStreams = List.of("3342_6_5700", "3342_5_5700");
        var action = MockMvcRequestBuilders.get("/api/v1/device/status/deviceId/{deviceId}/dataStreams",
                deviceId).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(dataStreams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void pushByRegionOfDataStream() throws Exception {
        var region = "威海智慧大厦";
        var dataStream = "3342_6_5700";
        var value = 1;
        var action = MockMvcRequestBuilders.put("/api/v1/device/status/region/{region}/dataStream/{dataStream}/value/{value}",
                region, dataStream, value).accept(MediaType.APPLICATION_JSON_UTF8);
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void pullByRegionOfDataStream() throws Exception {
        var region = "威海智慧大厦";
        var dataStream = "3342_6_5700";
        var action = MockMvcRequestBuilders.get("/api/v1/device/status/region/{region}/dataStream/{dataStream}",
                region, dataStream).accept(MediaType.APPLICATION_JSON_UTF8);
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void pullByRegionOfDataStreamWithAngle() throws Exception {
        var region = "威海智慧大厦";
        var dataStream = "3342_6_5700";
        var action = MockMvcRequestBuilders.get("/api/v1/device/status/region/{region}/dataStream/{dataStream}/angle",
                region, dataStream).accept(MediaType.APPLICATION_JSON_UTF8);
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void pushByRegionOfDataStreams() throws Exception {
        var region = "威海智慧大厦";
        var dataStreams = List.of("3342_6_5700", "3342_5_5700");
        var value = 1;
        var action = MockMvcRequestBuilders.put("/api/v1/device/status/region/{region}/dataStreams/value/{value}",
                region, value).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(dataStreams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void pullByRegionOfDataStreams() throws Exception {
        var region = "威海智慧大厦";
        var dataStreams = List.of("3342_6_5700", "3342_5_5700");
        var action = MockMvcRequestBuilders.post("/api/v1/device/status/region/{region}/dataStreams",
                region).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(dataStreams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void newDeviceMiniStar() throws Exception {
        var deviceId = 528130535;
        var deviceSubtitle = new Subtitle();
        deviceSubtitle.action = Subtitle.Action.SAVE;
        deviceSubtitle.beginTimestamp = MyDateTime.toTimestamp("2019-07-12 11:41:01", MyDateTime.format1);
        deviceSubtitle.endTimestamp = MyDateTime.toTimestamp("2019-07-12 13:41:01", MyDateTime.format1);
        deviceSubtitle.deviceId = deviceId;
        var effect = new Subtitle.Effect();
        effect.type = Subtitle.Effect.Type.HX;
        var rgb1 = new Subtitle.Effect.Rgb((short)255, (short)254, (short)253);
        var rgb2 = new Subtitle.Effect.Rgb((short)245, (short)244, (short)243);
        var rgb3 = new Subtitle.Effect.Rgb((short)235, (short)234, (short)233);
        var rgbs = new ArrayList<Subtitle.Effect.Rgb>();
        rgbs.add(rgb1);
        rgbs.add(rgb2);
        rgbs.add(rgb3);
        effect.colors = rgbs;
        deviceSubtitle.effect = effect;
        deviceSubtitle.speed = 90;
        deviceSubtitle.title = "test";
        deviceSubtitle.userId = "c9a45d5d972011e9b0790242c0a8b006";
        var action = MockMvcRequestBuilders.post("/api/v1/device/ministar/deviceId/{deviceId}",
                deviceId).accept(MediaType.APPLICATION_JSON_UTF8);
        var mapper = new ObjectMapper();
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsString(deviceSubtitle)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }

    @Test
    public void getRegionTree() throws Exception{
        var organId = "099060a6971911e9b0790242c0a8b006";
        var action = MockMvcRequestBuilders.get("/api/v1/region/tree/{organId}", organId).accept(MediaType.APPLICATION_JSON_UTF8);
        var result = this.mockMvc.perform(action.contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.json.id").value(4))
                .andReturn().getResponse().getContentAsString();
        log.debug(result);
    }
}
