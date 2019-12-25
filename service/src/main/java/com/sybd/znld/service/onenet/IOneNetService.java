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
    String ONENET_TOPIC_EXCHANGE = "ONENET_TOPIC_EXCHANGE";

    String ONENET_ONLINE_UP_QUEUE = "ONENET_ONLINE_UP_QUEUE";
    String ONENET_ONLINE_UP_ROUTING_KEY = "ONENET.ONLINE.UP";

    String ONENET_ONOFF_UP_QUEUE = "ONENET_ONOFF_UP_QUEUE";
    String ONENET_ONOFF_UP_ROUTING_KEY = "ONENET.ONOFF.UP";

    String ONENET_POSITION_UP_QUEUE = "ONENET_POSITION_UP_QUEUE";
    String ONENET_POSITION_UP_ROUTING_KEY = "ONENET.POSITION.UP";

    String ONENET_ANGLE_UP_QUEUE = "ONENET_ANGLE_UP_QUEUE";
    String ONENET_ANGLE_UP_ROUTING_KEY = "ONENET.ANGLE.UP";

    String ONENET_ENVIRONMENT_UP_QUEUE = "ONENET_ENVIRONMENT_UP_QUEUE";
    String ONENET_ENVIRONMENT_UP_ROUTING_KEY = "ONENET.ENVIRONMENT.UP";

    String ONENET_LIGHT_UP_QUEUE = "ONENET_LIGHT_UP_QUEUE";
    String ONENET_LIGHT_UP_ROUTING_KEY = "ONENET.LIGHT.UP";

    String ONENET_LIGHT_ALARM_QUEUE = "ONENET_LIGHT_ALARM_QUEUE";
    String ONENET_LIGHT_ALARM_ROUTING_KEY = "ONENET.LIGHT.ALARM";

    String ONENET_LIGHT_EXECUTION_QUEUE = "ONENET_LIGHT_EXECUTION_QUEUE";
    String ONENET_LIGHT_EXECUTION_ROUTING_KEY = "ONENET.LIGHT.EXECUTION";

    RawData extractUpMsg(String body);
    Integer getUpMsgType(String body);
    Integer getUpMsgStatus(String body);
    List<String> getUpMsgIds(String body);
    String getUpMsgImei(String body);
    Integer getUpMsgDeviceId(String body);
    Long getUpMsgAt(String body);


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
