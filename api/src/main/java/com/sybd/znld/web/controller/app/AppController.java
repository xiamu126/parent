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
                                           @RequestHeader(name = "app_version") String appVersion) {
        if(MyString.isEmptyOrNull(appName) || MyString.isEmptyOrNull(appVersion) || !CheckVersionResult.isValidVersion(appVersion)){
            return CheckVersionResult.fail("错误的参数");
        }
        var tmp = CheckVersionResult.getVersionNumber(appVersion);
        var appModel = this.lampService.getAppInfoByName("hd");
        if(appModel == null) return CheckVersionResult.fail("检测版本失败");
        var tmp1 = CheckVersionResult.getVersionNumber(appModel.version);
        if(tmp == null || tmp1 == null) return CheckVersionResult.fail("检测版本失败");
        if(CheckVersionResult.needUpdate(tmp, tmp1)){
            return CheckVersionResult.success(true, appModel.url);
        }
        return CheckVersionResult.success(false, null);
    }
}
