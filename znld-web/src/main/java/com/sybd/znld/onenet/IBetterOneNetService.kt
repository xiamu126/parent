package com.sybd.znld.onenet

import com.sybd.znld.onenet.dto.*
import java.time.LocalDateTime

interface IBetterOneNetService {
    fun execute(params: CommandParams): OneNetExecuteResult
    fun getOneNetKey(name: String): String
    fun getInstanceMap(deviceId: Int): Map<String, String>
    fun getDeviceById(deviceId: Int): GetDeviceResult
    fun getLastDataStream(deviceId: Int): GetLastDataStreamsResult
    fun getHistoryDataStream(deviceId: Int,
                             dataStreamId: String,
                             start: LocalDateTime,
                             end: LocalDateTime,
                             limit: Int?,
                             sort: String?,
                             cursor: String?): GetHistoryDataStreamResult

    fun getLastDataStreamById(deviceId: Int, dataStreamId: String): GetDataStreamByIdResult
    fun getLastDataStreamsByIds(deviceId: Int, vararg dataStreamIds: String): GetDataStreamsByIdsResult
    fun getWeightedData(deviceId: Int, dataStreamId: String, start: LocalDateTime, end: LocalDateTime): Double
}