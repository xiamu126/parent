package com.sybd.znld.web.controller.app;

import com.sybd.znld.web.controller.app.dto.CheckVersionResult;

public interface IAppController {
    CheckVersionResult checkVersion(String appName, String appVersion);
}
