package com.sybd.znld.onenet;

import com.sybd.znld.onenet.dto.OneNetKey;
import com.sybd.znld.service.OneNetConfigDeviceService;
import com.whatever.util.MyString;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;


@Slf4j
@Getter @Setter @ToString
@Component
@ConfigurationProperties(prefix = "znld.onenet")
public class OneNetConfig {
    private final OneNetConfigDeviceService onenetConfigDeviceService;
    private String getHistoryDataStreamUrl;
    private String postExecuteUrl;
    private String getLastDataStreamUrl;
    private String getDeviceUrl;
    private String getDataStreamByIdUrl;
    private String getDataStreamsByIdsUrl;

    @Autowired
    public OneNetConfig(OneNetConfigDeviceService onenetConfigDeviceService) {
        this.onenetConfigDeviceService = onenetConfigDeviceService;
    }

    String getDataStreamId(OneNetKey oneNetKey){
        return oneNetKey.getObjId().toString() + "_" + oneNetKey.getObjInstId().toString() + "_" + oneNetKey.getResId().toString();
    }

    String getImei(Integer deviceId){
        return this.onenetConfigDeviceService.getImeiByDeviceId(deviceId);
    }
    String getDesc(Integer objId, Integer objInstId, Integer resId){
        return this.onenetConfigDeviceService.getDescBy(objId, objInstId, resId);
    }

    String getApiKey(Integer deviceId){
        return this.onenetConfigDeviceService.getApiKeyByDeviceId(deviceId);
    }

    String getOneNetKey(String name){
        return this.onenetConfigDeviceService.getOneNetKey(name);
    }
    String getLastDataStreamUrl(Integer deviceId){
        return MyString.replace(getLastDataStreamUrl, deviceId.toString());
    }
    String getHistoryDataStreamUrl(Integer deviceId){
        return MyString.replace(getHistoryDataStreamUrl, deviceId.toString());
    }

    String getDataStreamUrl(Integer deviceId, String dataStreamId){
        return MyString.replace(getDataStreamByIdUrl, deviceId.toString(), dataStreamId);
    }
    String getDataStreamsByIdsUrl(Integer deviceId, String... dataStreamIds){
        var tmp = Arrays.stream(dataStreamIds).reduce((a,b)->a+","+b).orElse("");
        log.debug(tmp);
        return MyString.replace(getDataStreamsByIdsUrl, deviceId.toString(), tmp);
    }

    String getDeviceUrl(Integer deviceId){
        return MyString.replace(getDeviceUrl, deviceId.toString());
    }

    Map<String, String> getInstanceMap(Integer deviceId){
        return this.onenetConfigDeviceService.getInstanceMap(deviceId);
    }

    public enum  ExecuteCommand {
        ZNLD_HEART_BEAT("000"), //心跳
        ZNLD_SCREEN_OPEN("001"), //打开屏幕
        ZNLD_SCREEN_CLOSE("002"), //关闭屏幕
        ZNLD_QXZ_OPEN("003"), //打开气象站
        ZNLD_QXZ_CLOSE("004"), //关闭气象站
        ZNLD_QXZ_DATA_UPLOAD("101"), //气象站数据上传
        ZNLD_STATUS_QUERY("102"), //路灯状态查询
        ZNLD_LOCATION_QUERY("103"), //位置信息查询
        ZNLD_QX_UPLOAD_RATE("A"), //气象信息上传频率
        ZNLD_LOCATION_UPLOAD_RATE("B"), //位置信息上传频率
        ZNLD_STATUS_UPLOAD_RATE("C"); //路灯状态信息上传频率
        private String value;
        ExecuteCommand(String value){
            this.value = value;
        }
        public String getValue(){
            return this.value;
        }
    }

    public static String DESC = "DESC";
    public static String ASC = "ASC";
}