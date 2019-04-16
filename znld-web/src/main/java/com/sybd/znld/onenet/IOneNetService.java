package com.sybd.znld.onenet;

import com.sybd.onenet.model.OneNetKey;
import com.sybd.znld.model.dto.DeviceIdAndImei;
import com.sybd.znld.onenet.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IOneNetService {
    List<DeviceIdAndImei> getDeviceIdAndImei();
    String getDataStreamId(OneNetKey oneNetKey);
    String getImeiByDeviceId(Integer deviceId);
    String getApiKeyByDeviceId(Integer deviceId);
    OneNetExecuteResult execute(CommandParams params);
    Map<String, String> getResourceMapByDeviceId(Integer deviceId);
    GetDeviceResult getDeviceById(Integer deviceId);
    GetLastDataStreamsResult getLastDataStream(Integer deviceId);
    GetHistoryDataStreamResult getHistoryDataStream(Integer deviceId, String dataStreamId, LocalDateTime start, LocalDateTime end, Integer limit, String sort, String cursor);
    GetDataStreamByIdResult getLastDataStreamById(Integer deviceId, String dataStreamId);
    GetDataStreamsByIdsResult getLastDataStreamsByIds(Integer deviceId, String... dataStreamIds);
    double getWeightedData(Integer deviceId, String dataStreamId, LocalDateTime start, LocalDateTime end);
}
