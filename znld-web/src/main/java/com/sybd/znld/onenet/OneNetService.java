package com.sybd.znld.onenet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.onenet.dto.*;
import com.whatever.util.MyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OneNetService implements IOneNetService {

    private final Logger log = LoggerFactory.getLogger(OneNetService.class);
    private final OneNetConfig oneNetConfig;

    @Autowired
    public OneNetService(OneNetConfig oneNetConfig) {
        this.oneNetConfig = oneNetConfig;
    }

    @Override
    public GetLastDataStreamsResult getLastDataStream(Integer deviceId){
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        String url = oneNetConfig.getLastDataStreamUrl(deviceId);
        log.debug(url);
        ResponseEntity<GetLastDataStreamsResult> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetLastDataStreamsResult.class);
        return responseEntity.getBody();
    }

    private HttpEntity<String> getHttpEntity(Integer deviceId, MediaType mediaType){
        HttpHeaders headers = new HttpHeaders();
        headers.add("api-key", oneNetConfig.getApiKey(deviceId));
        headers.setContentType(mediaType);
        log.debug(oneNetConfig.getApiKey(deviceId));
        return new HttpEntity<>(null, headers);
    }

    private HttpEntity<String> getHttpEntity(Integer deviceId, MediaType mediaType, String body){
        HttpHeaders headers = new HttpHeaders();
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
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        String url = oneNetConfig.getHistoryDataStreamUrl(deviceId);
        HashMap<String, String> map = new HashMap<>();
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
        ResponseEntity<GetHistoryDataStreamResult> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetHistoryDataStreamResult.class);
        return responseEntity.getBody();
    }

    @Override
    public GetDataStreamByIdResult getLastDataStreamById(Integer deviceId, String dataStreamId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        String url = oneNetConfig.getDataStreamUrl(deviceId, dataStreamId);
        ResponseEntity<GetDataStreamByIdResult> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDataStreamByIdResult.class);
        return responseEntity.getBody();
    }

    @Override
    public GetDataStreamsByIdsResult getLastDataStreamsByIds(Integer deviceId, String... dataStreamIds) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8"));
        String url = oneNetConfig.getDataStreamsByIdsUrl(deviceId, dataStreamIds);
        ResponseEntity<GetDataStreamsByIdsResult> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDataStreamsByIdsResult.class);
        return responseEntity.getBody();
    }

    @Override
    public double getWeightedData(Integer deviceId, String dataStreamId, LocalDateTime start, LocalDateTime end) {
        GetHistoryDataStreamResult historyData = getHistoryDataStream(deviceId, dataStreamId, start, end, null, null, null);
        List<GetHistoryDataStreamResult.DataPoint> data = historyData.getData().getDataStreams().get(0).getDataPoints();
        double avg = data.stream().collect(Collectors.averagingDouble(d -> Double.parseDouble(d.getValue())));
        return avg;
    }

    @Override
    public OneNetExecuteResult execute(CommandParams params){
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            OneNetExecuteArgs executeEntity = new OneNetExecuteArgs(params.getCommand());
            String jsonBody = objectMapper.writeValueAsString(executeEntity);
            HttpEntity<String> httpEntity = getHttpEntity(params.getDeviceId(), MediaType.parseMediaType("application/json; charset=UTF-8"), jsonBody);
            String url = oneNetConfig.getPostExecuteUrl();
            url = url + params.toUrlString();
            ResponseEntity<OneNetExecuteResult> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, OneNetExecuteResult.class);
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
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity = getHttpEntity(deviceId, MediaType.parseMediaType("application/json; charset=UTF-8"));
        String url = oneNetConfig.getDeviceUrl(deviceId);
        ResponseEntity<GetDeviceResult> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GetDeviceResult.class);
        return responseEntity.getBody();
    }
}
