package com.sybd.znld.web.controller.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.util.MyNumber;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;

public class CheckVersionResult extends BaseApiResult {
    @JsonProperty("need_update")
    public Boolean needUpdate;
    public String url;
    @JsonProperty("version_code")
    public Integer versionCode;

    public CheckVersionResult(int code, String msg){
        super(code, msg);
        this.needUpdate = null;
        this.url = null;
    }
    public CheckVersionResult(int code, String msg, Boolean needUpdate, String url, Integer versionCode){
        super(code, msg);
        this.needUpdate = needUpdate;
        this.url = url;
        this.versionCode = versionCode;
    }

    public static boolean isValidVersion(String version){
        var matcher = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)$").matcher(version);
        if(!matcher.find()){
            return false;
        }
        var major = MyNumber.getLong(matcher.group(1));
        var minor = MyNumber.getLong(matcher.group(2));
        var compile = MyNumber.getLong(matcher.group(3));
        if(major == null || major < 0) return false;
        if(minor == null || minor < 0) return false;
        if(compile == null || compile < 0) return false;
        return true;
    }

    public static List<Long> getVersionNumber(String version){
        var matcher = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)$").matcher(version);
        if(!matcher.find()){
            return null;
        }
        var major = MyNumber.getLong(matcher.group(1));
        var minor = MyNumber.getLong(matcher.group(2));
        var compile = MyNumber.getLong(matcher.group(3));
        if(major == null || major < 0) return null;
        if(minor == null || minor < 0) return null;
        if(compile == null || compile < 0) return null;
        return List.of(major, minor, compile);
    }

    public static boolean needUpdate(List<Long> versionNumbers, List<Long> versionNumbersInDb){
        if(versionNumbers.get(0) > versionNumbersInDb.get(0)){
            return false;
        }else if(versionNumbers.get(0).equals(versionNumbersInDb.get(0)) && versionNumbers.get(1) > versionNumbersInDb.get(1)){
            return false;
        }
        else if(versionNumbers.get(0).equals(versionNumbersInDb.get(0)) && versionNumbers.get(1).equals(versionNumbersInDb.get(1)) && versionNumbers.get(2) > versionNumbersInDb.get(2)){
            return false;
        }
        else if(versionNumbers.get(0).equals(versionNumbersInDb.get(0)) && versionNumbers.get(1).equals(versionNumbersInDb.get(1)) && versionNumbers.get(2).equals(versionNumbersInDb.get(2))){
            return false;
        }
        return true;
    }

    public static CheckVersionResult success(boolean needUpdate, String url, Integer versionCode){
        return new CheckVersionResult(0, "", needUpdate, url, versionCode);
    }

    public static CheckVersionResult fail(String msg){
        return new CheckVersionResult(1, msg);
    }
}
