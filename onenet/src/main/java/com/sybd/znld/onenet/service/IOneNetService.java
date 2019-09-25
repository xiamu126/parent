package com.sybd.znld.onenet.service;

import com.sybd.znld.model.lamp.dto.DeviceIdAndImei;
import com.sybd.znld.model.onenet.DataPushModel;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.onenet.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IOneNetService {
    List<DeviceIdAndImei> getDeviceIdAndImei();
    String getDataStreamId(OneNetKey oneNetKey);
    String getImeiByDeviceId(Integer deviceId);
    String getApiKeyByDeviceId(Integer deviceId);
    BaseResult execute(CommandParams params);
    OfflineExecuteResult offlineExecute(CommandParams params);
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
    boolean isDeviceOnline(Integer deviceId);
    boolean addOrModifyDeviceOnOff(DataPushModel model);
    boolean addOrModifyDeviceLocation(DataPushModel model);
    boolean addOrModifyDeviceAngle(DataPushModel model);
}
