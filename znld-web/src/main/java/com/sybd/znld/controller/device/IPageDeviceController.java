package com.sybd.znld.controller.device;

import org.springframework.ui.Model;

public interface IPageDeviceController {
    String getCmdPage();
    String getViewPage(Integer deviceId, Model model);
    String getHeatMapPage(Model model);
    String getVideoPage(String cameraId, Model model);
}
