package com.sybd.znld.onenet;

import com.sybd.znld.onenet.dto.*;

import java.time.LocalDateTime;
import java.util.Map;

public interface IOneNetService {
    OneNetExecuteResult execute(CommandParams params);
    String getOneNetKey(String name);
    Map<String, String> getInstanceMap(Integer deviceId);
    GetDeviceResult getDeviceById(Integer deviceId);
    GetLastDataStreamsResult getLastDataStream(Integer deviceId);
    GetHistoryDataStreamResult getHistoryDataStream(Integer deviceId,
                                                    String dataStreamId,
                                                    LocalDateTime start,
                                                    LocalDateTime end,
                                                    Integer limit,
                                                    String sort,
                                                    String cursor);
    GetDataStreamByIdResult getLastDataStreamById(Integer deviceId, String dataStreamId);
    GetDataStreamsByIdsResult getLastDataStreamsByIds(Integer deviceId, String... dataStreamIds);
}
