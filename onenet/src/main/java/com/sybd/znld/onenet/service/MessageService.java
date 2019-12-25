package com.sybd.znld.onenet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.model.environment.RawData;
import com.sybd.znld.model.environment.RealTimeData;
import com.sybd.znld.model.lamp.LampAlarmModel;
import com.sybd.znld.model.lamp.LampStatisticsModel;
import com.sybd.znld.model.lamp.dto.LampAlarm;
import com.sybd.znld.model.lamp.dto.LampAlarmOutput;
import com.sybd.znld.model.lamp.dto.LampStatistic;
import com.sybd.znld.model.lamp.dto.LampStatistics;
import com.sybd.znld.model.onenet.Config;
import com.sybd.znld.model.onenet.DataPushModel;
import com.sybd.znld.model.onenet.dto.News;
import com.sybd.znld.model.onenet.dto.OnOffLineNews;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageService implements IMessageService {
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final RestTemplate restTemplate;
    private final DataEnvironmentMapper dataEnvironmentMapper;
    private final LampMapper lampMapper;
    private final LampStatisticsMapper lampStatisticsMapper;
    private final IOneNetService oneNetService;
    private final LampExecutionMapper lampExecutionMapper;
    private final LampStrategyMapper lampStrategyMapper;
    private final RabbitTemplate rabbitTemplate;
    private final LampAlarmMapper lampAlarmMapper;
    private final RegionMapper regionMapper;

    @Value("${baidu-ak}")
    private String baiduAK;

    @Autowired
    public MessageService(ObjectMapper objectMapper,
                          RedissonClient redissonClient,
                          OneNetResourceMapper oneNetResourceMapper,
                          RestTemplate restTemplate,
                          DataEnvironmentMapper dataEnvironmentMapper,
                          LampMapper lampMapper,
                          LampStatisticsMapper lampStatisticsMapper,
                          IOneNetService oneNetService,
                          LampExecutionMapper lampExecutionMapper,
                          LampStrategyMapper lampStrategyMapper,
                          RabbitTemplate rabbitTemplate,
                          LampAlarmMapper lampAlarmMapper,
                          RegionMapper regionMapper) {
        this.objectMapper = objectMapper;
        this.redissonClient = redissonClient;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.restTemplate = restTemplate;
        this.dataEnvironmentMapper = dataEnvironmentMapper;
        this.lampMapper = lampMapper;
        this.lampStatisticsMapper = lampStatisticsMapper;
        this.oneNetService = oneNetService;
        this.lampExecutionMapper = lampExecutionMapper;
        this.lampStrategyMapper = lampStrategyMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.lampAlarmMapper = lampAlarmMapper;
        this.regionMapper = regionMapper;
    }

    @Override
    public RawData extractUpMsg(String body) {
        String ds = null;
        try {
            ds = JsonPath.read(body, "$.ds_id");
        } catch (Exception ignored) {
        }
        Object value = null;
        try {
            value = JsonPath.read(body, "$.value");
        } catch (Exception ignored) {
        }
        LocalDateTime at = null;
        try {
            Long tmp = JsonPath.read(body, "$.at");
            var zoneId = ZoneId.of("Asia/Shanghai");
            at = MyDateTime.toLocalDateTime(tmp, zoneId);
        } catch (Exception ignored) {
        }
        Integer deviceId = null;
        try {
            deviceId = JsonPath.read(body, "$.dev_id");
        } catch (Exception ignored) {
        }
        String imei = null;
        try {
            imei = JsonPath.read(body, "$.imei");
        } catch (Exception ignored) {
        }
        var rawData = new RawData();
        rawData.ds = ds;
        rawData.value = value;
        rawData.at = at;
        rawData.deviceId = deviceId;
        rawData.imei = imei;
        return rawData;
    }

    @Override
    public Integer getUpMsgType(String body) {
        Integer type = null;
        try {
            type = JsonPath.read(body, "$.type");
        } catch (Exception ignored) { }
        return type;
    }

    @Override
    public Integer getUpMsgStatus(String body) {
        Integer status = null;
        try {
            status = JsonPath.read(body, "$.status");
        } catch (Exception ignored) { }
        return status;
    }

    @Override
    public List<String> getUpMsgIds(String body) {
        try {
            String ds = JsonPath.read(body, "$.ds_id");
            var ids = ds.split("_");
            if (ids.length != 3) {
                return null;
            }
            return Arrays.stream(ids).collect(Collectors.toList());
        } catch (Exception ignored) { }
        return null;
    }

    @Override
    public String getUpMsgImei(String body) {
        String imei = null;
        try {
            imei = JsonPath.read(body, "$.imei");
        } catch (Exception ignored) { }
        return imei;
    }

    @Override
    public Integer getUpMsgDeviceId(String body) {
        Integer deviceId = null;
        try {
            deviceId = JsonPath.read(body, "$.dev_id");
        } catch (Exception ignored) {
        }
        return deviceId;
    }

    @Override
    public Long getUpMsgAt(String body) {
        Long at = null;
        try {
            at = JsonPath.read(body, "$.at");
        } catch (Exception ignored) {
        }
        return at;
    }

    @Override
    public News onOff(String body) {
        var rawData = this.extractUpMsg(body);
        if(rawData == null) return null;
        var ids = rawData.getIds();
        if (ids == null || ids.isEmpty()) {
            log.debug("不能解析DataStreamId：" + rawData.ds);
            return null;
        }
        var name = this.oneNetResourceMapper.selectNameByDataStreamId(ids.get(0), ids.get(1), ids.get(2));
        if (name == null) { // 有些DataStreamId是没有定义的，如北斗定位的误差之类
            log.debug("通过DataStreamId获取相应的资源名称为空：" + rawData.ds);
            return null;
        }
        var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(rawData.imei));
        var realTimeData = new RealTimeData();
        realTimeData.describe = name;
        realTimeData.value = rawData.value;
        realTimeData.at = MyDateTime.toTimestamp(rawData.at);
        map.put(name, realTimeData); // 更新实时缓存

        var news = new News();
        news.deviceId = rawData.deviceId;
        news.imei = rawData.imei;
        news.name = name;
        news.value = rawData.value;
        news.at = MyDateTime.toTimestamp(rawData.at);
        return news;
    }

    @Override
    public OnOffLineNews onOffLine(String body) {
        var status = this.getUpMsgStatus(body);
        var imei = this.getUpMsgImei(body);
        var news = new OnOffLineNews();
        news.deviceId = this.getUpMsgDeviceId(body);
        news.imei = imei;
        news.at = this.getUpMsgAt(body);
        if (status != null) {
            if (status == 0) {
                news.isOnline = false;
            } else if (status == 1) {
                news.isOnline = true;
            } else {
                news.isOnline = null;
            }
            if (imei != null) {
                var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(imei));
                map.put(Config.REDIS_MAP_KEY_IS_ONLINE, news.isOnline);
            }
        }
        return news;
    }

    @Override
    public News position(String body) {
        var rawData = this.extractUpMsg(body);
        if(rawData == null) return null;
        var ids = rawData.getIds();
        if (ids == null || ids.isEmpty()) {
            log.debug("不能解析DataStreamId：" + rawData.ds);
            return null;
        }
        var name = this.oneNetResourceMapper.selectNameByDataStreamId(ids.get(0), ids.get(1), ids.get(2));
        if (name == null) { // 有些DataStreamId是没有定义的，如北斗定位的误差之类
            log.debug("通过DataStreamId获取相应的资源名称为空：" + rawData.ds);
            return null;
        }
        var tmp = MyNumber.getDouble(rawData.value.toString());
        if (tmp != null && tmp != 0.0) {
            var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(rawData.imei));
            var realTimeData = new RealTimeData();
            realTimeData.describe = name;
            realTimeData.value = rawData.value;
            realTimeData.at = MyDateTime.toTimestamp(rawData.at);
            map.put(name, realTimeData); // 更新实时缓存
            // 经度和纬度是分开传过来的，所以再发送实时数据的时候，必须要保证经度和纬度的数据都存在时才发送
            // 判断经度和纬度的数据是否为同一批数据，看这两个数据的时差，如果是30秒内的，就是同一批数据
            Object lng = null;
            Object lat = null;
            LocalDateTime at = null;
            if (name.contains("经度")) {
                // 是经度数据，那么就从缓存里查纬度数据
                realTimeData = (RealTimeData) map.get(name.replace("经", "纬"));
                if (realTimeData != null) {
                    lng = rawData.value;
                    lat = realTimeData.value;
                    at = MyDateTime.toLocalDateTime(realTimeData.at);
                }
            } else {
                realTimeData = (RealTimeData) map.get(name.replace("纬", "经"));
                if (realTimeData != null) {
                    lng = realTimeData.value;
                    lat = rawData.value;
                    at = MyDateTime.toLocalDateTime(realTimeData.at);
                }

            }
            if (at == null) {
                return null;
            }
            if (lng != null && lat != null) {
                try {
                    var builder = UriComponentsBuilder
                            .fromHttpUrl("http://api.map.baidu.com/geoconv/v1/")
                            .queryParam("coords", lng + "," + lat)
                            .queryParam("from", 1)
                            .queryParam("to", 5)
                            .queryParam("ak", this.baiduAK);
                    if(MyString.isEmptyOrNull(this.baiduAK)) {
                        log.error("未指定百度ak");
                    }
                    var converted = this.restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, Object.class);
                    var repoBody = converted.getBody();
                    if (repoBody != null) {
                        int status = JsonPath.read(repoBody, "$.status");
                        if (status == 0) {
                            var baiduLng = JsonPath.read(repoBody, "$.result[0].x");
                            var baiduLat = JsonPath.read(repoBody, "$.result[0].y");
                            map.put(Config.REDIS_MAP_KEY_BAIDU_LNG, new RealTimeData(baiduLng, MyDateTime.toTimestamp(at)));
                            map.put(Config.REDIS_MAP_KEY_BAIDU_LAT, new RealTimeData(baiduLat, MyDateTime.toTimestamp(at)));
                        }
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    log.error(ExceptionUtils.getStackTrace(ex));
                }
                var seconds = Duration.between(at, rawData.at).getSeconds();
                if (Math.abs(seconds) <= 30) { // 是同一批的经纬度
                    var news = new News();
                    news.deviceId = rawData.deviceId;
                    news.imei = rawData.imei;
                    news.name = name;
                    news.value = lng + "," + lat;
                    news.at = MyDateTime.toTimestamp(rawData.at);
                    return news;
                }
            }
        } else {
            log.debug("经纬度不合法");
        }
        return null;
    }

    @Override
    public News angle(String body) {
        var rawData = this.extractUpMsg(body);
        if(rawData == null) return null;
        var ids = rawData.getIds();
        if (ids == null || ids.isEmpty()) {
            log.debug("不能解析DataStreamId：" + rawData.ds);
            return null;
        }
        var name = this.oneNetResourceMapper.selectNameByDataStreamId(ids.get(0), ids.get(1), ids.get(2));
        if (name == null) { // 有些DataStreamId是没有定义的，如北斗定位的误差之类
            log.debug("通过DataStreamId获取相应的资源名称为空：" + rawData.ds);
            return null;
        }
        var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(rawData.imei));
        var realTimeData = new RealTimeData();
        realTimeData.describe = name;
        realTimeData.value = rawData.value;
        realTimeData.at = MyDateTime.toTimestamp(rawData.at);
        map.put(name, realTimeData); // 更新实时缓存

        var news = new News();
        news.deviceId = rawData.deviceId;
        news.imei = rawData.imei;
        news.name = name;
        news.value = rawData.value;
        news.at = MyDateTime.toTimestamp(rawData.at);
        return news;
    }

    @Override
    public News environment(String body) {
        var rawData = this.extractUpMsg(body);
        if(rawData == null) return null;
        var ids = rawData.getIds();
        if (ids == null || ids.isEmpty()) {
            log.debug("不能解析DataStreamId：" + rawData.ds);
            return null;
        }
        var name = this.oneNetResourceMapper.selectNameByDataStreamId(ids.get(0), ids.get(1), ids.get(2));
        if (name == null) { // 有些DataStreamId是没有定义的，如北斗定位的误差之类
            log.debug("通过DataStreamId获取相应的资源名称为空：" + rawData.ds);
            return null;
        }
        var data = new DataPushModel();
        data.deviceId = rawData.deviceId;
        data.imei = rawData.imei;
        data.datastreamId = rawData.ds;
        data.name = name;
        data.value = rawData.value;
        data.at = rawData.at;
        this.dataEnvironmentMapper.insert(data); // // 环境数据需要保存入数据库

        var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(rawData.imei));
        var realTimeData = new RealTimeData();
        realTimeData.describe = name;
        realTimeData.value = rawData.value;
        realTimeData.at = MyDateTime.toTimestamp(rawData.at);
        map.put(name, realTimeData); // 更新实时缓存

        var news = new News();
        news.deviceId = rawData.deviceId;
        news.imei = rawData.imei;
        news.name = name;
        news.value = rawData.value;
        news.at = MyDateTime.toTimestamp(rawData.at);
        return news;
    }

    // 收到硬件传来的单灯数据后，更新缓存中的数据，并且返回一个用于推送到前端的对象
    @Override
    public LampStatistic statistics(String body) {
        var rawData = this.extractUpMsg(body);
        if(rawData == null) return null;
        var ids = rawData.getIds();
        if (ids == null || ids.isEmpty()) {
            log.debug("不能解析DataStreamId：" + rawData.ds);
            return null;
        }
        var name = this.oneNetResourceMapper.selectNameByDataStreamId(ids.get(0), ids.get(1), ids.get(2));
        if (name == null) { // 有些DataStreamId是没有定义的，如北斗定位的误差之类
            log.debug("通过DataStreamId获取相应的资源名称为空：" + rawData.ds);
            return null;
        }
        try {
            rawData.value = rawData.value.toString().replaceAll("'","\"");
            // 首先把数据存入redis
            var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(rawData.imei));
            map.put(Config.REDIS_MAP_KEY_ONENET_UP_MSG, body); // 把onenet传来的原始数据放入缓存
            var obj = this.objectMapper.readValue(rawData.value.toString(), LampStatistics.class);
            var energy = MyNumber.getDouble(map.get(Config.REDIS_MAP_KEY_ENERGY)); // 上一次累计的电量
            var ep = obj.EP.get(1);
            if(energy == null) {
                if(ep == null || ep <= 0) {
                    energy = 0.0;
                } else {
                    energy = ep; // 更新为这一次的数据
                }
            }else {
                if(ep != null && ep > 0) {
                    energy = energy + ep; // 把这一次的数据累计上去
                }
            }
            map.put(Config.REDIS_MAP_KEY_IS_LIGHT, obj.B > 0); // 当前灯的亮度状态
            map.put(Config.REDIS_MAP_KEY_BRIGHTNESS, obj.B); // 当前的亮度值
            map.put(Config.REDIS_MAP_KEY_ENERGY, energy); // 这里存放的是上一次清零到目前为止的累计电量
            var isOnline = (Boolean) map.get(Config.REDIS_MAP_KEY_IS_ONLINE);
            if(isOnline == null) { // 如果设备的在线状态未知，则手动刷新下
                isOnline = this.oneNetService.isDeviceOnline(rawData.imei);
                map.put(Config.REDIS_MAP_KEY_IS_ONLINE, isOnline); // 如果设备的在线状态为空，则更新设备的在线状态
            }
            map.put(Config.REDIS_MAP_KEY_ONENET_UP_MSG_AT, MyDateTime.toTimestamp(rawData.at)); // onenet原始数据的更新时间

            // 最后将收到的数据推送到页面
            var lamp = this.lampMapper.selectByImei(rawData.imei);
            var statistics = new LampStatistic();
            var msg = new LampStatistic.Message();
            msg.id = lamp.id;
            msg.voltage =  new LampStatistic.Message.ValueError<>(obj.V, LampStatistic.Message.isVoltageError(obj.V));
            msg.brightness = new LampStatistic.Message.ValueError<>(obj.B, obj.B != null && obj.B >= 0 && obj.B <= 100);
            msg.electricity = new LampStatistic.Message.ValueError<>(obj.I.get(1), LampStatistic.Message.isElectricityError(obj.I.get(1)));
            msg.energy = new LampStatistic.Message.ValueError<>(obj.EP.get(1), false);
            msg.power = new LampStatistic.Message.ValueError<>(obj.PP.get(1), false);
            var pp = obj.PP.get(1);
            var ps = obj.PS.get(1);
            if(ps == null || ps <= 0) {
                msg.powerFactor = new LampStatistic.Message.ValueError<>(0.0, true);
            } else {
                msg.powerFactor = new LampStatistic.Message.ValueError<>(pp / ps, false);
            }
            msg.rate = new LampStatistic.Message.ValueError<>(obj.HZ, LampStatistic.Message.isRateError(obj.HZ));
            msg.updateTime = MyDateTime.toTimestamp(rawData.at);
            msg.angleX = obj.X;
            msg.angleY = obj.Y;
            msg.co = obj.CO;
            msg.deviceId = rawData.deviceId;
            msg.hddp = obj.hddp;
            msg.hgt = obj.hgt;
            msg.humidity = obj.Ua;
            msg.temperature = obj.TP;
            msg.imei = rawData.imei;
            msg.innerHumidity = obj.HU;
            msg.innerTemperature = obj.TP;
            msg.lat = obj.lat;
            msg.lon = obj.lon;
            msg.no2 = obj.NO2;
            msg.o3 = obj.O3;
            msg.pm10 = obj.PM10;
            msg.pm25 = obj.PM25;
            msg.isSwitchOn = obj.RL.stream().map(t -> t == 1).collect(Collectors.toList());
            msg.so2 = obj.SO2;
            msg.spd = obj.spd;
            msg.stn = obj.stn;
            msg.totalEnergy = MyNumber.getDouble(map.get(Config.REDIS_MAP_KEY_TOTAL_ENERGY));
            statistics.message = msg;
            map.put(Config.REDIS_MAP_KEY_IS_FAULT, msg.voltage.isError || msg.electricity.isError || msg.rate.isError); // 当前灯的故障状态
            var lampRegionOrganId = this.lampMapper.selectLampRegionOrganIdByImei(lamp.imei);
            var region = this.regionMapper.selectById(lampRegionOrganId.regionId);
            if(msg.voltage.isError) {
                var errorMsg = "电压异常";
                var lampAlarmModel = new LampAlarmModel();
                lampAlarmModel.at = LocalDateTime.now();
                lampAlarmModel.content = errorMsg;
                lampAlarmModel.lampId = lamp.id;
                lampAlarmModel.lampName = lamp.deviceName;
                lampAlarmModel.organId = region.organizationId;
                lampAlarmModel.regionId = region.id;
                lampAlarmModel.regionName = region.name;
                lampAlarmModel.type = LampAlarmModel.AlarmType.COMMON;
                lampAlarmModel.status = LampAlarmModel.Status.UNCONFIRMED;
                if(this.lampAlarmMapper.insert(lampAlarmModel) > 0) {
                    var lampAlarm = new LampAlarm();
                    var lampAlarmOutput = new LampAlarmOutput();
                    lampAlarmOutput.id = lampAlarmModel.id;
                    lampAlarmOutput.type = LampAlarmModel.AlarmType.COMMON.getDescribe();
                    lampAlarmOutput.status = LampAlarmModel.Status.UNCONFIRMED.getDescribe();
                    lampAlarmOutput.at = MyDateTime.toTimestamp(LocalDateTime.now());
                    lampAlarmOutput.content = errorMsg;
                    lampAlarmOutput.lampId = lamp.id;
                    lampAlarmOutput.lampName = lamp.deviceName;
                    lampAlarmOutput.regionName = region.name;
                    lampAlarm.message = lampAlarmOutput;
                    var alarmMsg = this.objectMapper.writeValueAsString(lampAlarm);
                    this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_ALARM_MSG_LIGHT_ROUTING_KEY, alarmMsg);
                }
            }
            if(msg.electricity.isError) {
                var errorMsg = "电流异常";
                var lampAlarmModel = new LampAlarmModel();
                lampAlarmModel.at = LocalDateTime.now();
                lampAlarmModel.content = errorMsg;
                lampAlarmModel.lampId = lamp.id;
                lampAlarmModel.lampName = lamp.deviceName;
                lampAlarmModel.organId = region.organizationId;
                lampAlarmModel.regionId = region.id;
                lampAlarmModel.regionName = region.name;
                lampAlarmModel.type = LampAlarmModel.AlarmType.COMMON;
                lampAlarmModel.status = LampAlarmModel.Status.UNCONFIRMED;
                if(this.lampAlarmMapper.insert(lampAlarmModel) > 0) {
                    var lampAlarm = new LampAlarm();
                    var lampAlarmOutput = new LampAlarmOutput();
                    lampAlarmOutput.id = lampAlarmModel.id;
                    lampAlarmOutput.type = LampAlarmModel.AlarmType.COMMON.getDescribe();
                    lampAlarmOutput.status = LampAlarmModel.Status.UNCONFIRMED.getDescribe();
                    lampAlarmOutput.at = MyDateTime.toTimestamp(LocalDateTime.now());
                    lampAlarmOutput.content = errorMsg;
                    lampAlarmOutput.lampId = lamp.id;
                    lampAlarmOutput.lampName = lamp.deviceName;
                    lampAlarmOutput.regionName = region.name;
                    lampAlarm.message = lampAlarmOutput;
                    var alarmMsg = this.objectMapper.writeValueAsString(lampAlarm);
                    this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_ALARM_MSG_LIGHT_ROUTING_KEY, alarmMsg);
                }
            }
            if(msg.rate.isError) {
                var errorMsg = "频率异常";
                var lampAlarmModel = new LampAlarmModel();
                lampAlarmModel.at = LocalDateTime.now();
                lampAlarmModel.content = errorMsg;
                lampAlarmModel.lampId = lamp.id;
                lampAlarmModel.lampName = lamp.deviceName;
                lampAlarmModel.organId = region.organizationId;
                lampAlarmModel.regionId = region.id;
                lampAlarmModel.regionName = region.name;
                lampAlarmModel.type = LampAlarmModel.AlarmType.COMMON;
                lampAlarmModel.status = LampAlarmModel.Status.UNCONFIRMED;
                if (this.lampAlarmMapper.insert(lampAlarmModel) > 0) {
                    var lampAlarm = new LampAlarm();
                    var lampAlarmOutput = new LampAlarmOutput();
                    lampAlarmOutput.id = lampAlarmModel.id;
                    lampAlarmOutput.type = LampAlarmModel.AlarmType.COMMON.getDescribe();
                    lampAlarmOutput.status = LampAlarmModel.Status.UNCONFIRMED.getDescribe();
                    lampAlarmOutput.at = MyDateTime.toTimestamp(LocalDateTime.now());
                    lampAlarmOutput.content = errorMsg;
                    lampAlarmOutput.lampId = lamp.id;
                    lampAlarmOutput.lampName = lamp.deviceName;
                    lampAlarmOutput.regionName = region.name;
                    lampAlarm.message = lampAlarmOutput;
                    var alarmMsg = this.objectMapper.writeValueAsString(lampAlarm);
                    this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_ALARM_MSG_LIGHT_ROUTING_KEY, alarmMsg);
                }
            }
            return statistics;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    // 每隔一段时间，将缓存中的数据更新到数据库中
    @Override
    public void scheduledStatistics() {
        var lamps = this.lampMapper.selectAll();
        if(lamps == null || lamps.isEmpty()) return;
        for(var lamp : lamps) {
            if(lamp.protocolVersion < 2) continue; // 协议版本号小于2意味着不支持单灯
            var map = this.redissonClient.getMap(Config.getRedisRealtimeKey(lamp.imei));
            if(map == null || map.isEmpty()) continue;
            var lampRegionOrganId = this.lampMapper.selectLampRegionOrganIdByImei(lamp.imei);
            // 更新总的电能
            var totalEnergy = MyNumber.getDouble(map.get(Config.REDIS_MAP_KEY_TOTAL_ENERGY));
            var energy = MyNumber.getDouble(map.get(Config.REDIS_MAP_KEY_ENERGY));
            if(energy == null) {
                energy = 0.0;
            }
            if(totalEnergy == null) {
                // 手动通过数据库中的数据得到一个总的累计值
                var tmp = this.lampStatisticsMapper.selectTotalEnergyByLampId(lamp.id);
                if(tmp != null) {
                    map.put(Config.REDIS_MAP_KEY_TOTAL_ENERGY, tmp);
                }
            } else {
                totalEnergy += energy;
                map.put(Config.REDIS_MAP_KEY_TOTAL_ENERGY, totalEnergy);
            }
            // 在线状态
            var isOnline = (Boolean) map.get(Config.REDIS_MAP_KEY_IS_ONLINE);
            if(isOnline == null) {
                isOnline = this.oneNetService.isDeviceOnline(lamp.imei);
                map.put(Config.REDIS_MAP_KEY_IS_ONLINE, isOnline);
            }
            // 亮灯状态
            var isFault = (Boolean) map.get(Config.REDIS_MAP_KEY_IS_FAULT);
            if(isFault == null) {
                // 如果缓存中没有故障状态，则默认时没有故障的
                isFault = false;
            }
            var isLight = (Boolean) map.get(Config.REDIS_MAP_KEY_IS_LIGHT);
            if(isLight == null) {
                // 如果缓存中没有亮灯状态，则默认是开着的（提高亮灯率）
                isLight = true;
            }
            // 更新数据库
            var model = new LampStatisticsModel();
            model.lampId = lampRegionOrganId.lampId;
            model.regionId = lampRegionOrganId.regionId;
            model.organId = lampRegionOrganId.organId;
            model.isOnline = isOnline;
            model.isLight = isLight;
            model.isFault = isFault; // 故障暂时不做判断
            model.energy = energy; // 到目前为止累计电能
            model.updateTime = LocalDateTime.now();
            this.lampStatisticsMapper.insert(model);
            map.put(Config.REDIS_MAP_KEY_LAST_STATISTICS_UPDATE_TIME, MyDateTime.toTimestamp(LocalDateTime.now()));
            map.put(Config.REDIS_MAP_KEY_ENERGY, 0); // 清空累计，也就是我只保存这一个小时的电量，下个周期从0开始重新计算
        }
    }
}
