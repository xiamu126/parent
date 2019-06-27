package com.sybd.znld.model.onenet;

public class Command {
    public static final String ZNLD_HEART_BEAT = "000"; // 心跳
    public static final String ZNLD_SCREEN_OPEN = "001"; // 打开屏幕
    public static final String ZNLD_SCREEN_CLOSE = "002"; // 关闭屏幕
    public static final String ZNLD_QXZ_OPEN = "003"; // 打开气象站
    public static final String ZNLD_QXZ_CLOSE = "004"; // 关闭气象站
    public static final String ZNLD_QXZ_DATA_UPLOAD = "101"; // 气象站数据上传
    public static final String ZNLD_STATUS_QUERY = "102"; // 路灯状态查询
    public static final String ZNLD_LOCATION_QUERY = "103"; // 位置信息查询
    public static final String ZNLD_HANDSHAKE = "104"; // 路灯与平台握手
    public static final String ZNLD_QX_UPLOAD_RATE = "A"; // 气象信息上传频率
    public static final String ZNLD_LOCATION_UPLOAD_RATE = "B"; // 位置信息上传频率
    public static final String ZNLD_STATUS_UPLOAD_RATE = "C"; // 路灯状态信息上传频率
    public static final String ZNLD_QXZ_START_REPORTING = "200"; // 环境监测开始上传信息
    public static final String ZNLD_QXZ_STOP_REPORTING = "201"; // 环境监测停止上传信息
    public static final String ZNLD_LOCATION_START_REPORTING = "202"; // 位置信息开始上传
    public static final String ZNLD_LOCATION_STOP_REPORTING = "203"; // 位置信息停止上传
    public static final String ZNLD_DD_EXECUTE = "204"; // 上传景观灯执行内容
}
