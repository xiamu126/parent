package com.sybd.znld.onenet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.onenet.dto.*;
import com.whatever.util.MyString;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OneNetService implements IOneNetService {

    private final OneNetConfig oneNetConfig;

    @Autowired
    public OneNetService(OneNetConfig oneNetConfig) {
        this.oneNetConfig = oneNetConfig;
    }

    @Override
    public GetLastDataStreamsResult getLastDataStream(Integer deviceId){
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = oneNetConfig.getLastDataStreamUrl(deviceId);
        log.debug(url);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetLastDataStreamsResult.class);
        return responseEntity.getBody();
    }

    private HttpEntity<String> getHttpEntity(Integer deviceId, MediaType mediaType){
        var headers = new HttpHeaders();
        headers.add("api-key", oneNetConfig.getApiKey(deviceId));
        headers.setContentType(mediaType);
        log.debug(oneNetConfig.getApiKey(deviceId));
        return new HttpEntity<>(null, headers);
    }

    private HttpEntity<String> getHttpEntity(Integer deviceId, MediaType mediaType, String body){
        var headers = new HttpHeaders();
        headers.add("api-key", oneNetConfig.getApiKey(deviceId));
        headers.setContentType(mediaType);
        return new HttpEntity<>(body, headers);
    }

    public String getDataStreamId(OneNetKey oneNetKey){
        return oneNetConfig.getDataStreamId(oneNetKey);
    }

    public String getDesc(Integer objId, Integer objInstId, Integer resId){
        return oneNetConfig.getDesc(objId, objInstId, resId);
    }

    @Override
    public GetHistoryDataStreamResult getHistoryDataStream(Integer deviceId,
                                                           String dataStreamId,
                                                           LocalDateTime start,
                                                           LocalDateTime end,
                                                           Integer limit,
                                                           String sort,
                                                           String cursor) {
        if(end != null && start.isAfter(end)){
            return null;
        }
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = oneNetConfig.getHistoryDataStreamUrl(deviceId);
        var map = new HashMap<String, String>();
        if(start != null){
            map.put("start", start.toString());
        }
        if(end != null){
            map.put("end", end.toString());
        }
        if(dataStreamId != null){
            map.put("datastream_id", dataStreamId);
        }
        if(limit != null){
            map.put("limit", limit.toString());
        }
        if(sort != null){
            map.put("sort", sort);
        }
        if(cursor != null){
            map.put("cursor", cursor);
        }
        url += MyString.toUrlParams(map);
        log.debug(url);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetHistoryDataStreamResult.class);
        return responseEntity.getBody();
    }

    @Override
    public GetDataStreamByIdResult getLastDataStreamById(Integer deviceId, String dataStreamId) {
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = oneNetConfig.getDataStreamUrl(deviceId, dataStreamId);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDataStreamByIdResult.class);
        return responseEntity.getBody();
    }

    @Override
    public GetDataStreamsByIdsResult getLastDataStreamsByIds(Integer deviceId, String... dataStreamIds) {
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        var url = oneNetConfig.getDataStreamsByIdsUrl(deviceId, dataStreamIds);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDataStreamsByIdsResult.class);
        return responseEntity.getBody();
    }

    @Override
    public double getWeightedData(Integer deviceId, String dataStreamId, LocalDateTime start, LocalDateTime end) {
        var historyData = getHistoryDataStream(deviceId, dataStreamId, start, end, null, null, null);
        var data = historyData.getData().getDatastreams()[0].getDatapoints();
        var avg = Arrays.stream(data).collect(Collectors.averagingDouble(d -> Double.parseDouble(d.getValue())));
        return avg;
    }

    @Override
    public OneNetExecuteResult execute(CommandParams params){
        try {
            var restTemplate = new RestTemplate();
            var objectMapper = new ObjectMapper();
            var executeEntity = new OneNetExecuteArgs(params.getCommand());
            var jsonBody = objectMapper.writeValueAsString(executeEntity);
            var httpEntity = getHttpEntity(params.getDeviceId(), MediaType.parseMediaType("application/json; charset=UTF-8"), jsonBody);
            var url = oneNetConfig.getPostExecuteUrl();
            url = url + params.toUrlString();
            var responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, OneNetExecuteResult.class);
            return responseEntity.getBody();
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());

        }
        return null;
    }

    @Override
    public String getOneNetKey(String name) {
        return this.oneNetConfig.getOneNetKey(name);
    }

    @Override
    public Map<String, String> getInstanceMap(Integer deviceId) {
        return this.oneNetConfig.getInstanceMap(deviceId);
    }

    @Override
    public GetDeviceResult getDeviceById(Integer deviceId) {
        var restTemplate = new RestTemplate();
        var httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/json; charset=UTF-8"));
        var url = oneNetConfig.getDeviceUrl(deviceId);
        var responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDeviceResult.class);
        return responseEntity.getBody();
    }
}
