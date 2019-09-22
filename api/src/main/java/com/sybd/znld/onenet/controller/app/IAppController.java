package com.sybd.znld.onenet.controller.app;

import com.sybd.znld.onenet.controller.app.dto.CheckVersionResult;
import com.sybd.znld.onenet.controller.app.dto.GetAppProfileResult;

public interface IAppController {
    CheckVersionResult checkVersion(String appName, Integer appVersionCode);
    GetAppProfileResult getProfile(String appName, String userId);
}
