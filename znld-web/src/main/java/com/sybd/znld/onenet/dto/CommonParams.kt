package com.sybd.znld.onenet.dto;

open class CommonParams(var deviceId: Int, var imei: String, var objId: Int, var resId: Int, var objInstId: Int, var timeout: Int) {
    constructor(deviceId: Int, imei: String, key: OneNetKey, timeout: Int): this(deviceId, imei, key.objId, key.resId, key.objInstId, timeout)
    fun toUrlString():String{
        return "?imei=$imei&obj_id=$objId&res_id=$resId&obj_inst_id=$objInstId&timeout=$timeout"
    }
}
