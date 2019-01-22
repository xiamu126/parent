package com.sybd.znld.onenet

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.sybd.znld.onenet.dto.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Component
class BetterOneNetService @Autowired constructor(private val oneNetConfig: OneNetConfig): IBetterOneNetService {

    val log = LoggerFactory.getLogger(this.javaClass)!!

    override fun execute(params: CommandParams): OneNetExecuteResult? {
        try {
            val restTemplate = RestTemplate()
            val objectMapper = ObjectMapper()
            val executeEntity = OneNetExecuteArgs(params.command)
            val jsonBody = objectMapper.writeValueAsString(executeEntity)
            val httpEntity = getHttpEntity(params.deviceId, MediaType.parseMediaType("application/json; charset=UTF-8"), jsonBody)
            var url = oneNetConfig.postExecuteUrl
            url += params.toUrlString()
            val responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, OneNetExecuteResult::class.java)
            return responseEntity.body
        } catch (ex: JsonProcessingException) {
            log.error(ex.message)
        }
        return null
    }

    private fun getHttpEntity(deviceId: Int, mediaType: MediaType, body: String): HttpEntity<out String> {
        val headers = HttpHeaders()
        headers.add("api-key", oneNetConfig.getApiKey(deviceId))
        headers.contentType = mediaType
        return HttpEntity(body, headers)
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