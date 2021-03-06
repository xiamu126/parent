package com.sybd.znld.service.onenet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sybd.znld.mapper.lamp.DataEnvironmentMapper;
import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.mapper.lamp.LampStatisticsMapper;
import com.sybd.znld.mapper.lamp.OneNetResourceMapper;
import com.sybd.znld.model.environment.RawData;
import com.sybd.znld.model.environment.RealTimeData;
import com.sybd.znld.model.lamp.LampStatisticsModel;
import com.sybd.znld.model.lamp.dto.LampStatistic;
import com.sybd.znld.model.lamp.dto.LampStatistics;
import com.sybd.znld.model.onenet.Config;
import com.sybd.znld.model.onenet.DataPushModel;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.lamp.dto.DeviceIdAndImei;
import com.sybd.znld.model.onenet.dto.*;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConfigurationProperties(prefix = "znld.onenet")
public class OneNetService implements IOneNetService {
    private final LampMapper lampMapper;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RedissonClient redissonClient;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final DataEnvironmentMapper dataEnvironmentMapper;
    private final LampStatisticsMapper lampStatisticsMapper;

    @Value("${baidu-ak}")
    private String baiduAK;

    @Getter @Setter public String getHistoryDataStreamUrl;
    @Getter @Setter public String postExecuteUrl;
    @Getter @Setter public String offlineExecuteUrl;
    @Getter @Setter public String getLastDataStreamUrl;
    @Getter @Setter public String getDeviceUrl;
    @Getter @Setter public String getDataStreamByIdUrl;
    @Getter @Setter public String getDataStreamsByIdsUrl;
    @Getter @Setter public String writeValueUrl;
    @Getter @Setter public String readValueUrl;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public OneNetService(LampMapper lampMapper,
                         ObjectMapper objectMapper,
                         RestTemplate restTemplate,
                         RedissonClient redissonClient,
                         OneNetResourceMapper oneNetResourceMapper,
                         DataEnvironmentMapper dataEnvironmentMapper,
                         LampStatisticsMapper lampStatisticsMapper) {
        this.lampMapper = lampMapper;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.redissonClient = redissonClient;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.dataEnvironmentMapper = dataEnvironmentMapper;
        this.lampStatisticsMapper = lampStatisticsMapper;
    }

    @Override
    public List<DeviceIdAndImei> getDeviceIdAndImei() {
        return this.lampMapper.selectAllDeviceIdAndImei();
    }

    @Override
    public String getDataStreamId(OneNetKey oneNetKey){
        return oneNetKey.objId + "_" + oneNetKey.objInstId + "_" + oneNetKey.resId;
    }

    @Override
    public String getImeiByDeviceId(Integer deviceId){
        return this.lampMapper.selectImeiByDeviceId(deviceId);
    }

    @Override
    public String getApiKeyByImei(String imei){
        return this.lampMapper.selectApiKeyByImei(imei);
    }

    @Async
    @Override
    public Future<BaseResult> executeAsync(CommandParams params) {
        try {
            if(params == null){
                log.error("传入的参数为空");
                return new AsyncResult<>(new BaseResult(1, "参数错误"));
            }
            if(MyString.isEmptyOrNull(params.imei)){
                log.error("错误的imei["+params.imei+"]");
                return new AsyncResult<>(new BaseResult(1, "参数错误"));
            }
            if(!params.oneNetKey.isValid()){
                log.error("错误的oneNetKey["+params.oneNetKey.toDataStreamId()+"]");
                return new AsyncResult<>(new BaseResult(1, "参数错误"));
            }
            if(MyString.isEmptyOrNull(params.command)){
                log.error("错误的command，为空");
                return new AsyncResult<>(new BaseResult(1, "参数错误"));
            }
            var lamp = this.lampMapper.selectByImei(params.imei);
            if(lamp == null){
                log.error("指定的imei["+params.imei+"]在数据库中找不到");
                return new AsyncResult<>(new BaseResult(1, "参数错误"));
            }
            var executeEntity = new OneNetExecuteParams();
            executeEntity.args = params.command;
            var jsonBody = objectMapper.writeValueAsString(executeEntity);
            var httpEntity = getHttpEntity(params.imei, MediaType.parseMediaType("application/json; charset=UTF-8"), jsonBody);
            var url = this.postExecuteUrl;
            url = url + params.toUrlString();
            var responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, BaseResult.class);
            return new AsyncResult<>(responseEntity.getBody());
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return new AsyncResult<>(new BaseResult(1, "执行命令发生异常"));
    }

    private String getLastDataStreamUrl(Integer deviceId){
        return MyString.replace(getLastDataStreamUrl, deviceId.toString());
    }
    private String getHistoryDataStreamUrl(Integer deviceId){
        return MyString.replace(getHistoryDataStreamUrl, deviceId.toString());
    }
    private String getDataStreamUrl(Integer deviceId, String dataStreamId){
        return MyString.replace(getDataStreamByIdUrl, deviceId.toString(), dataStreamId);
    }
    private String getDataStreamsByIdsUrl(Integer deviceId, String... dataStreamIds){
        var tmp = Arrays.stream(dataStreamIds).reduce((a, b)->a+","+b).orElse("");
        return MyString.replace(getDataStreamsByIdsUrl, deviceId.toString(), tmp);
    }
    private String getDeviceUrl(Integer deviceId){
        return MyString.replace(getDeviceUrl, deviceId.toString());
    }

    @Override
    public Map<String, String> getResourceMapByDeviceId(Integer deviceId){
        return this.lampMapper.selectResourceMapByDeviceId(deviceId);
    }

    public static String DESC = "DESC";
    public static String ASC = "ASC";

    private HttpEntity<String> getHttpEntity(String imei, MediaType mediaType){
        return getHttpEntity(imei, mediaType, null);
    }

    private HttpEntity<String> getHttpEntity(String imei, MediaType mediaType, String body){
        var headers = new HttpHeaders();
        headers.add("api-key", this.getApiKeyByImei(imei));
        headers.setContentType(mediaType);
        return new HttpEntity<>(body, headers);
    }

    @Override
    public GetLastDataStreamsResult getLastDataStream(Integer deviceId){
        var model = this.lampMapper.selectByDeviceId(deviceId);
        if(model == null) return null;
        if(model.linkTo > 0){
            deviceId = model.linkTo;
        }
        var httpEntity = getHttpEntity(model.imei, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = this.getLastDataStreamUrl(deviceId);
        log.debug(url);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetLastDataStreamsResult.class);
        return responseEntity.getBody();
    }

    @Override
    public GetHistoryDataStreamResult getHistoryDataStream(Integer deviceId, OneNetKey oneNetKey, LocalDateTime start) {
        return this.getHistoryDataStream(deviceId, oneNetKey.toDataStreamId(), start, null, null, null, null);
    }

    @Override
    public GetHistoryDataStreamResult
    getHistoryDataStream(Integer deviceId, String dataStreamId, LocalDateTime start, LocalDateTime end, Integer limit, String sort, String cursor) {
        if(end != null && start.isAfter(end)) return null;
        var model = this.lampMapper.selectByDeviceId(deviceId);
        if(model == null) return null;
        if(model.linkTo > 0){
            deviceId = model.linkTo;
        }

        var httpEntity = getHttpEntity(model.imei, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = this.getHistoryDataStreamUrl(deviceId);

        var map = new HashMap<String, String>();
        if(start != null) map.put("start", start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if(end != null) map.put("end", end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if(dataStreamId != null) map.put("datastream_id", dataStreamId);
        if(limit != null) map.put("limit", limit.toString());
        if(sort != null) map.put("sort", sort);
        if(cursor != null) map.put("cursor", cursor);

        url += MyString.toUrlParams(map);
        log.debug(url);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetHistoryDataStreamResult.class);
        return responseEntity.getBody();
    }

    @Override
    public GetDataStreamByIdResult getLastDataStreamById(Integer deviceId, String dataStreamId) {
        var model = this.lampMapper.selectByDeviceId(deviceId);
        if(model == null) return null;
        if(model.linkTo > 0){
            deviceId = model.linkTo;
        }
        var httpEntity = getHttpEntity(model.imei, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = this.getDataStreamUrl(deviceId, dataStreamId);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDataStreamByIdResult.class);
        return responseEntity.getBody();
    }

    @Override
    public GetDataStreamsByIdsResult getLastDataStreamsByIds(Integer deviceId, String... dataStreamIds) {
        var model = this.lampMapper.selectByDeviceId(deviceId);
        if(model == null) return null;
        if(model.linkTo > 0){
            deviceId = model.linkTo;
        }
        var httpEntity = getHttpEntity(model.imei, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = this.getDataStreamsByIdsUrl(deviceId, dataStreamIds);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDataStreamsByIdsResult.class);
        return responseEntity.getBody();
    }

    @Override
    public double getWeightedData(Integer deviceId, String dataStreamId, LocalDateTime start, LocalDateTime end) {
        var model = this.lampMapper.selectByDeviceId(deviceId);
        if(model != null && model.linkTo > 0){
            deviceId = model.linkTo;
        }
        var historyData = getHistoryDataStream(deviceId, dataStreamId, start, end, null, null, null);
        var data = historyData.getData().getDataStreams().get(0).getDataPoints();
        return data.stream().collect(Collectors.averagingDouble(d -> Double.parseDouble(d.getValue())));
    }

    @Override
    public OneNetExecuteResult getValue(Integer deviceId, OneNetKey oneNetKey) {
        var model = this.lampMapper.selectByDeviceId(deviceId);
        if(model == null) return null;
        if(model.linkTo > 0){
            deviceId = model.linkTo;
        }
        var lamp = this.lampMapper.selectByDeviceId(deviceId);
        if(lamp == null) return null;
        try{
            var httpEntity = getHttpEntity(model.imei, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
            var url = readValueUrl + "?imei="+lamp.imei + "&obj_id="+oneNetKey.objId + "&obj_inst_id="+oneNetKey.objInstId+"&res_id"+oneNetKey.resId;
            var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, OneNetExecuteResult.class);
            return responseEntity.getBody();
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return new OneNetExecuteResult(1, "读取数据发生异常");
    }

    @Override
    public BaseResult setValue(Integer deviceId, OneNetKey oneNetKey, Object value) {
        var model = this.lampMapper.selectByDeviceId(deviceId);
        if(model == null) return null;
        if(model.linkTo > 0){
            deviceId = model.linkTo;
        }
        var lamp = this.lampMapper.selectByDeviceId(deviceId);
        if(lamp == null) return null;
        try {
            var url = writeValueUrl + "?imei="+lamp.imei + "&obj_id="+oneNetKey.objId + "&obj_inst_id="+oneNetKey.objInstId + "&mode=1";
            var param = new OneNetWriteParams(oneNetKey.resId, null, value);
            var jsonBody = this.objectMapper.writeValueAsString(param);
            var httpEntity = getHttpEntity(model.imei, MediaType.parseMediaType("application/json; charset=UTF-8"), jsonBody);
            var responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, BaseResult.class);
            return responseEntity.getBody();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return new BaseResult(1, "写入数据发生异常");
    }

    @Override
    public Boolean isDeviceOnline(String imei) {
        if(MyString.isEmptyOrNull(imei)) return null;
        var model = this.lampMapper.selectByImei(imei);
        if(model == null) return null;
        var httpEntity = getHttpEntity(model.imei, MediaType.parseMediaType("application/json; charset=UTF-8"));
        var url = this.getDeviceUrl(model.deviceId);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Object.class);
        var obj = responseEntity.getBody();
        return JsonPath.parse(obj).read("$.data.online", Boolean.class);
    }

    @Override
    public Boolean isDeviceOnline(Integer deviceId) {
        if(!MyNumber.isPositive(deviceId)) return null;
        var model = this.lampMapper.selectByDeviceId(deviceId);
        if(model == null) return null;
        var httpEntity = getHttpEntity(model.imei, MediaType.parseMediaType("application/json; charset=UTF-8"));
        var url = this.getDeviceUrl(deviceId);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Object.class);
        var obj = responseEntity.getBody();
        return JsonPath.parse(obj).read("$.data.online", Boolean.class);
    }

    @Override
    public BaseResult execute(CommandParams params){
        try {
            if(params == null){
                log.error("传入的参数为空");
                return new BaseResult(1, "参数错误");
            }
            if(MyString.isEmptyOrNull(params.imei)){
                log.error("错误的imei["+params.imei+"]");
                return new BaseResult(1, "参数错误");
            }
            if(!params.oneNetKey.isValid()){
                log.error("错误的oneNetKey["+params.oneNetKey.toDataStreamId()+"]");
                return new BaseResult(1, "参数错误");
            }
            if(MyString.isEmptyOrNull(params.command)){
                log.error("错误的command，为空");
                return new BaseResult(1, "参数错误");
            }
            var lamp = this.lampMapper.selectByImei(params.imei);
            if(lamp == null){
                log.error("指定的imei["+params.imei+"]在数据库中找不到");
                return new BaseResult(1, "参数错误");
            }
            var executeEntity = new OneNetExecuteParams();
            if(MyString.isEmptyOrNull(params.command)){
                return new BaseResult(1, "执行命令为空");
            }
            executeEntity.args = params.command;
            var jsonBody = objectMapper.writeValueAsString(executeEntity);
            var httpEntity = getHttpEntity(params.imei, MediaType.parseMediaType("application/json; charset=UTF-8"), jsonBody);
            var url = this.postExecuteUrl;
            url = url + params.toUrlString();
            log.debug(url);
            var responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, BaseResult.class);
            return responseEntity.getBody();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return new BaseResult(1, "执行命令发生异常");
    }

    @Override
    public OfflineExecuteResult offlineExecute(OfflineCommandParams params) {
        try {
            if(params == null){
                log.error("传入的参数为空");
                return new OfflineExecuteResult(1, "参数错误");
            }
            if(MyString.isEmptyOrNull(params.imei)){
                log.error("错误的imei["+params.imei+"]");
                return new OfflineExecuteResult(1, "参数错误");
            }
            if(!params.oneNetKey.isValid()){
                log.error("错误的oneNetKey["+params.oneNetKey.toDataStreamId()+"]");
                return new OfflineExecuteResult(1, "参数错误");
            }
            if(MyString.isEmptyOrNull(params.command)){
                log.error("错误的command，为空");
                return new OfflineExecuteResult(1, "参数错误");
            }
            var lamp = this.lampMapper.selectByImei(params.imei);
            if(lamp == null){
                log.error("指定的imei["+params.imei+"]在数据库中找不到");
                return new OfflineExecuteResult(1, "参数错误");
            }
            var executeEntity = new OneNetExecuteParams();
            executeEntity.args = params.command;
            var jsonBody = objectMapper.writeValueAsString(executeEntity);
            var httpEntity = getHttpEntity(params.imei, MediaType.parseMediaType("application/json; charset=UTF-8"), jsonBody);
            var url = this.offlineExecuteUrl;
            url = url + params.toUrlString();
            log.debug(url);
            var responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, OfflineExecuteResult.class);
            return responseEntity.getBody();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return new OfflineExecuteResult(1, "执行离线命令发生异常");
    }

    @Override
    public GetDeviceResult getDeviceById(Integer deviceId) {
        var model = this.lampMapper.selectByDeviceId(deviceId);
        if(model == null) return null;
        if(model.linkTo > 0){
            deviceId = model.linkTo;
        }
        var httpEntity = getHttpEntity(model.imei, MediaType.parseMediaType("application/json; charset=UTF-8"));
        var url = this.getDeviceUrl(deviceId);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDeviceResult.class);
        return responseEntity.getBody();
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
}
