package com.sybd.znld.web.controller.app;

import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import com.sybd.znld.web.controller.app.dto.CheckVersionResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Slf4j
@Api(tags = "app接口")
@RestController
@RequestMapping("/api/v1/app")
public class AppController implements IAppController {
    private final ILampService lampService;

    @Autowired
    public AppController(ILampService lampService) {
        this.lampService = lampService;
    }

    @ApiOperation(value = "检测更新")
    @PostMapping(value = "version", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public CheckVersionResult checkVersion(@RequestHeader(name = "app_name") String appName,
                                           @RequestHeader(name = "app_version_code") Integer appVersionCode) {
        if(MyString.isEmptyOrNull(appName) || appVersionCode == null){
            return CheckVersionResult.fail("错误的参数");
        }
        var appModel = this.lampService.getAppInfoByName("hd");
        if(appModel == null) return CheckVersionResult.fail("检测版本失败");
        if(appVersionCode < appModel.versionCode){
            return CheckVersionResult.success(true, appModel.url, appModel.versionCode);
        }
        return CheckVersionResult.success(false, null, null);
    }
}
