package com.sybd.znld.onenet;

import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.onenet.dto.*;
import com.sybd.znld.service.znld.dto.DeviceIdAndIMEI;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public interface IOneNetService {
    Map<Integer, String> getDeviceIdAndIMEI();
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
