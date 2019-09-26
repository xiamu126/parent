package com.sybd.znld.web.controller.app;

import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.util.MyString;
import com.sybd.znld.onenet.controller.app.dto.CheckVersionResult;
import com.sybd.znld.onenet.controller.app.dto.GetAppProfileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "APP接口")
@RestController
@RequestMapping("/api/v1/app")
public class AppController implements IAppController {
    private final ILampService lampService;
    private final MongoClient mongoClient;

    @Autowired
    public AppController(ILampService lampService, MongoClient mongoClient) {
        this.lampService = lampService;
        this.mongoClient = mongoClient;
    }

    @ApiOperation(value = "检测更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appName", value = "app名称", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "appVersionCode", value = "app版本", required = true, dataType = "int", paramType = "header")
    })
    @PostMapping(value = "version", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public CheckVersionResult checkVersion(@RequestHeader(name = "app_name") String appName, @RequestHeader(name = "app_version_code") Integer appVersionCode) {
        if(MyString.isEmptyOrNull(appName) || appVersionCode == null){
            return CheckVersionResult.fail("错误的参数");
        }
        var appModel = this.lampService.getAppInfoByName(appName);
        if(appModel == null) return CheckVersionResult.fail("检测版本失败");
        if(appVersionCode < appModel.versionCode){
            return CheckVersionResult.success(true, appModel.url, appModel.apiUrl, appModel.versionCode);
        }
        return CheckVersionResult.success(false, null, appModel.apiUrl, null);
    }

    @PostMapping(value = "profile", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public GetAppProfileResult getProfile(@RequestHeader(name = "app_name") String appName, @RequestHeader(name = "user_id") String userId) {
        var result = new GetAppProfileResult();
        if(!MyString.isUuid(userId) || MyString.isEmptyOrNull(appName)){
            result.code = 1;
            result.msg = "错误的参数";
            return result;
        }
        var db = mongoClient.getDatabase( "test" );
        var c1 = db.getCollection("com.sybd.znld.account.profile");
        var filter = new BasicDBObject();
        filter.put("id", userId);
        var doc = c1.find(filter).first();
        if(doc == null){
            result.code = 1;
            result.msg = "获取配置失败";
            return result;
        }
        var tmp = doc.get("app");
        var apiUrl = JsonPath.read(tmp, "$.hd.api_url");
        if(apiUrl == null){
            result.code = 1;
            result.msg = "获取配置失败";
            return result;
        }
        result.code = 0;
        result.msg = "";
        result.apiUrl = apiUrl.toString();
        result.appName = appName;
        return result;
    }
}
