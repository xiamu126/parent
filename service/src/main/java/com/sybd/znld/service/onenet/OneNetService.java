package com.sybd.znld.service.onenet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.lamp.dto.DeviceIdAndImei;
import com.sybd.znld.model.onenet.dto.*;
import com.sybd.znld.util.MyString;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConfigurationProperties(prefix = "znld.onenet")
public class OneNetService implements IOneNetService {
    private final LampMapper lampMapper;
    private final ObjectMapper objectMapper;

    @Getter @Setter public String getHistoryDataStreamUrl;
    @Getter @Setter public String postExecuteUrl;
    @Getter @Setter public String getLastDataStreamUrl;
    @Getter @Setter public String getDeviceUrl;
    @Getter @Setter public String getDataStreamByIdUrl;
    @Getter @Setter public String getDataStreamsByIdsUrl;
    @Getter @Setter public Command command;
    @Getter @Setter public String writeValueUrl;
    @Getter @Setter public String readValueUrl;

    @Getter @Setter
    public static class Command{
        public String ZNLD_HEART_BEAT;
        public String ZNLD_SCREEN_OPEN;
        public String ZNLD_SCREEN_CLOSE;
        public String ZNLD_QXZ_OPEN;
        public String ZNLD_QXZ_CLOSE;
        public String ZNLD_QXZ_DATA_UPLOAD;
        public String ZNLD_STATUS_QUERY;
        public String ZNLD_LOCATION_QUERY;
        public String ZNLD_HANDSHAKE;
        public String ZNLD_QX_UPLOAD_RATE;
        public String ZNLD_LOCATION_UPLOAD_RATE;
        public String ZNLD_STATUS_UPLOAD_RATE;
        public String ZNLD_QXZ_START_REPORTING;
        public String ZNLD_QXZ_STOP_REPORTING;
        public String ZNLD_LOCATION_START_REPORTING;
        public String ZNLD_LOCATION_STOP_REPORTING;
        public String ZNLD_DD_EXECUTE;
    }

    // spring不支持静态变量注入
    private static String ZNLD_HEART_BEAT;
    private static String ZNLD_SCREEN_OPEN;
    private static String ZNLD_SCREEN_CLOSE;
    private static String ZNLD_QXZ_OPEN;
    private static String ZNLD_QXZ_CLOSE;
    private static String ZNLD_QXZ_DATA_UPLOAD;
    private static String ZNLD_STATUS_QUERY;
    private static String ZNLD_LOCATION_QUERY;
    private static String ZNLD_HANDSHAKE;
    private static String ZNLD_QX_UPLOAD_RATE;
    private static String ZNLD_LOCATION_UPLOAD_RATE;
    private static String ZNLD_STATUS_UPLOAD_RATE;
    private static String ZNLD_QXZ_START_REPORTING;
    private static String ZNLD_QXZ_STOP_REPORTING;
    private static String ZNLD_LOCATION_START_REPORTING;
    private static String ZNLD_LOCATION_STOP_REPORTING;
    private static String ZNLD_DD_EXECUTE;

    @PostConstruct
    private void init(){
        ZNLD_HEART_BEAT = command.ZNLD_HEART_BEAT;
        ZNLD_SCREEN_OPEN = command.ZNLD_SCREEN_OPEN;
        ZNLD_SCREEN_CLOSE = command.ZNLD_SCREEN_CLOSE;
        ZNLD_QXZ_OPEN = command.ZNLD_QXZ_OPEN;
        ZNLD_QXZ_CLOSE = command.ZNLD_QXZ_CLOSE;
        ZNLD_QXZ_DATA_UPLOAD = command.ZNLD_QXZ_DATA_UPLOAD;
        ZNLD_STATUS_QUERY = command.ZNLD_STATUS_QUERY;
        ZNLD_LOCATION_QUERY = command.ZNLD_LOCATION_QUERY;
        ZNLD_HANDSHAKE = command.ZNLD_HANDSHAKE;
        ZNLD_QX_UPLOAD_RATE = command.ZNLD_QX_UPLOAD_RATE;
        ZNLD_LOCATION_UPLOAD_RATE = command.ZNLD_LOCATION_UPLOAD_RATE;
        ZNLD_STATUS_UPLOAD_RATE = command.ZNLD_STATUS_UPLOAD_RATE;
        ZNLD_QXZ_START_REPORTING = command.ZNLD_QXZ_START_REPORTING;
        ZNLD_QXZ_STOP_REPORTING = command.ZNLD_QXZ_STOP_REPORTING;
        ZNLD_LOCATION_START_REPORTING = command.ZNLD_LOCATION_START_REPORTING;
        ZNLD_LOCATION_STOP_REPORTING = command.ZNLD_LOCATION_STOP_REPORTING;
        ZNLD_DD_EXECUTE = command.ZNLD_DD_EXECUTE;
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public OneNetService(LampMapper lampMapper, ObjectMapper objectMapper) {
        this.lampMapper = lampMapper;
        this.objectMapper = objectMapper;
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
    public String getApiKeyByDeviceId(Integer deviceId){
        return this.lampMapper.selectApiKeyByDeviceId(deviceId);
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

    private HttpEntity<String> getHttpEntity(Integer deviceId, MediaType mediaType){
        return getHttpEntity(deviceId, mediaType, null);
    }

    private HttpEntity<String> getHttpEntity(Integer deviceId, MediaType mediaType, String body){
        var headers = new HttpHeaders();
        headers.add("api-key", this.getApiKeyByDeviceId(deviceId));
        headers.setContentType(mediaType);
        return new HttpEntity<>(body, headers);
    }

    @Override
    public GetLastDataStreamsResult getLastDataStream(Integer deviceId){
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = this.getLastDataStreamUrl(deviceId);
        log.debug(url);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetLastDataStreamsResult.class);
        return responseEntity.getBody();
    }

    @Override
    public GetHistoryDataStreamResult
    getHistoryDataStream(Integer deviceId, String dataStreamId, LocalDateTime start, LocalDateTime end, Integer limit, String sort, String cursor) {
        if(end != null && start.isAfter(end)) return null;
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
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
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = this.getDataStreamUrl(deviceId, dataStreamId);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDataStreamByIdResult.class);
        return responseEntity.getBody();
    }

    @Override
    public GetDataStreamsByIdsResult getLastDataStreamsByIds(Integer deviceId, String... dataStreamIds) {
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = this.getDataStreamsByIdsUrl(deviceId, dataStreamIds);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDataStreamsByIdsResult.class);
        return responseEntity.getBody();
    }

    @Override
    public double getWeightedData(Integer deviceId, String dataStreamId, LocalDateTime start, LocalDateTime end) {
        var historyData = getHistoryDataStream(deviceId, dataStreamId, start, end, null, null, null);
        var data = historyData.getData().getDataStreams().get(0).getDataPoints();
        return data.stream().collect(Collectors.averagingDouble(d -> Double.parseDouble(d.getValue())));
    }

    @Override
    public String getStringValue(Integer deviceId, OneNetKey oneNetKey) {
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = this.getDataStreamUrl(deviceId, oneNetKey.toDataStreamId());
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        return responseEntity.getBody();
    }

    @Override
    public BaseResult setStringValue(Integer deviceId, OneNetKey oneNetKey, String value) {
        var lamp = this.lampMapper.selectByDeviceId(deviceId);
        if(lamp == null) return null;

        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = writeValueUrl + "?ime="+lamp.imei + "&obj_id="+oneNetKey.objId + "&obj_inst_id="+oneNetKey.objInstId + "&mode=1";
        try {
            var param = new OneNetWriteParams();

            var jsonBody = this.objectMapper.writeValueAsString(null);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        var param = new CommandParams();
        param.deviceId = deviceId;
        param.imei = lamp.imei;
        param.oneNetKey = oneNetKey;
        param.command = value;
        return execute(param);
    }

    @Override
    public OneNetExecuteResult execute(CommandParams params){
        try {
            var restTemplate = new RestTemplate();
            var objectMapper = new ObjectMapper();
            var executeEntity = new OneNetExecuteParams();
            executeEntity.args = params.command;
            var jsonBody = objectMapper.writeValueAsString(executeEntity);
            var httpEntity = getHttpEntity(params.getDeviceId(), MediaType.parseMediaType("application/json; charset=UTF-8"), jsonBody);
            var url = this.postExecuteUrl;
            url = url + params.toUrlString();
            var responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, OneNetExecuteResult.class);
            return responseEntity.getBody();
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    @Override
    public GetDeviceResult getDeviceById(Integer deviceId) {
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/json; charset=UTF-8"));
        var url = this.getDeviceUrl(deviceId);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDeviceResult.class);
        return responseEntity.getBody();
    }
}
