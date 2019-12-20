package com.sybd.znld.service.onenet;

import com.sybd.znld.model.environment.RawData;
import com.sybd.znld.model.lamp.dto.LampStatistic;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.lamp.dto.DeviceIdAndImei;
import com.sybd.znld.model.onenet.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface IOneNetService {
    String ONENET_UP_MSG_ONLINE_QUEUE = "ONENET_UP_MSG_ONLINE_QUEUE";
    String ONENET_UP_MSG_ONOFF_QUEUE = "ONENET_UP_MSG_ONOFF_QUEUE";
    String ONENET_UP_MSG_POSITION_QUEUE = "ONENET_UP_MSG_POSITION_QUEUE";
    String ONENET_UP_MSG_ANGLE_QUEUE = "ONENET_UP_MSG_ANGLE_QUEUE";
    String ONENET_UP_MSG_ENVIRONMENT_QUEUE = "ONENET_UP_MSG_ENVIRONMENT_QUEUE";
    String ONENET_UP_MSG_LIGHT_QUEUE = "ONENET_UP_MSG_LIGHT_QUEUE";
    String ONENET_TOPIC_EXCHANGE = "ONENET_TOPIC_EXCHANGE";
    String ONENET_UP_MSG_ONLINE_ROUTING_KEY = "ONENET.UPMSG.ONLINE";
    String ONENET_UP_MSG_ONOFF_ROUTING_KEY = "ONENET.UPMSG.ONOFF";
    String ONENET_UP_MSG_POSITION_ROUTING_KEY = "ONENET.UPMSG.POSITION";
    String ONENET_UP_MSG_ANGLE_ROUTING_KEY = "ONENET.UPMSG.ANGLE";
    String ONENET_UP_MSG_ENVIRONMENT_ROUTING_KEY = "ONENET.UPMSG.ENVIRONMENT";
    String ONENET_UP_MSG_LIGHT_ROUTING_KEY = "ONENET.UPMSG.LIGHT";

    List<DeviceIdAndImei> getDeviceIdAndImei();
    String getDataStreamId(OneNetKey oneNetKey);
    String getImeiByDeviceId(Integer deviceId);
    String getApiKeyByImei(String imei);
    Future<BaseResult> executeAsync(CommandParams params);
    BaseResult execute(CommandParams params);
    OfflineExecuteResult offlineExecute(OfflineCommandParams params);
    Map<String, String> getResourceMapByDeviceId(Integer deviceId);
    GetDeviceResult getDeviceById(Integer deviceId);
    GetLastDataStreamsResult getLastDataStream(Integer deviceId);
    GetHistoryDataStreamResult getHistoryDataStream(Integer deviceId, OneNetKey oneNetKey, LocalDateTime start);
    GetHistoryDataStreamResult getHistoryDataStream(Integer deviceId, String dataStreamId, LocalDateTime start, LocalDateTime end, Integer limit, String sort, String cursor);
    GetDataStreamByIdResult getLastDataStreamById(Integer deviceId, String dataStreamId);
    GetDataStreamsByIdsResult getLastDataStreamsByIds(Integer deviceId, String... dataStreamIds);
    double getWeightedData(Integer deviceId, String dataStreamId, LocalDateTime start, LocalDateTime end);
    OneNetExecuteResult getValue(Integer deviceId, OneNetKey oneNetKey);
    BaseResult setValue(Integer deviceId, OneNetKey oneNetKey, Object value);
    Boolean isDeviceOnline(String imei);
    Boolean isDeviceOnline(Integer deviceId);
}
