package com.sybd.znld.onenet.dto;

class GetHistoryDataStreamResult(errno: Int, error: String): BaseResult(errno, error) {

    fun isOk(): Boolean{
        val tmp = data?.let { it.count > 0 }
        return errno == 0 && tmp != null && tmp
    }

    var data: Data? = null

    inner class Data{
        var cursor: String = ""
        var count: Int = 0
        var datastreams: Array<DataStream>? = null
        inner class DataStream{
            var id: String = ""
            var datapoints: Array<DataPoint>? = null
            inner class DataPoint {
                var at: String = ""
                var value: String = ""
            }
        }
    }
}
