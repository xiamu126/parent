package com.sybd.znld.onenet.dto;

class CommandParams(deviceId: Int, imei: String, objId: Int, resId: Int, objInstId: Int, timeout: Int, var command: String)
    : CommonParams(deviceId, imei, objId, resId, objInstId, timeout){
    constructor(deviceId: Int, imei: String, oneNetKey: OneNetKey ,timeout: Int, command: String)
            : this(deviceId, imei, oneNetKey.objId, oneNetKey.resId, oneNetKey.objInstId, timeout, command)
}
