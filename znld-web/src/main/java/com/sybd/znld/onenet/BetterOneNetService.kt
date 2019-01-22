package com.sybd.znld.onenet

import com.sybd.znld.onenet.dto.*
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Slf4j
@Component
class BetterOneNetService @Autowired constructor(private val oneNetConfig: OneNetConfig): IBetterOneNetService {

    override fun execute(params: CommandParams): OneNetExecuteResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOneNetKey(name: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInstanceMap(deviceId: Int): Map<String, String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDeviceById(deviceId: Int): GetDeviceResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastDataStream(deviceId: Int): GetLastDataStreamsResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHistoryDataStream(deviceId: Int, dataStreamId: String, start: LocalDateTime, end: LocalDateTime, limit: Int?, sort: String?, cursor: String?): GetHistoryDataStreamResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastDataStreamById(deviceId: Int, dataStreamId: String): GetDataStreamByIdResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastDataStreamsByIds(deviceId: Int, vararg dataStreamIds: String): GetDataStreamsByIdsResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWeightedData(deviceId: Int, dataStreamId: String, start: LocalDateTime, end: LocalDateTime): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}