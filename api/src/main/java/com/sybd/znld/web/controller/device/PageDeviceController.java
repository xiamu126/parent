package com.sybd.znld.web.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.web.controller.device.BaseDeviceController;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@Controller
public class PageDeviceController extends BaseDeviceController implements IPageDeviceController {
    @Autowired
    public PageDeviceController(RedissonClient redissonClient,
                                IOneNetService oneNet,
                                ILampService lampService,
                                ProjectConfig projectConfig,
                                IUserService userService) {
        super(redissonClient, oneNet, lampService, projectConfig, userService);
    }

    @RequestMapping(value = "/cmd", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    @Override
    public String getCmdPage(){
        return "cmd";
    }

    @RequestMapping(value = "/view/{deviceId:^[1-9]\\d*$}", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    @Override
    public String getViewPage(@PathVariable(name = "deviceId") Integer deviceId, Model model){
        var ret = this.oneNet.getLastDataStream(deviceId);
        if(ret.errno == 0){
            var dataStreams = ret.getData().getDevices().get(0).getDataStreams();
            var weidu = dataStreams.stream()
                    .filter(dataStream -> dataStream.getId().equals("3336_0_5513"))
                    .findFirst().orElse(null);
            var jingdu = dataStreams.stream()
                    .filter(dataStream -> dataStream.getId().equals("3336_0_5514"))
                    .findFirst().orElse(null);
            model.addAttribute("deviceId", deviceId);
            model.addAttribute("weidu", weidu);
            model.addAttribute("jingdu", jingdu);
        }//否则发生错误
        return "view";
    }

    @RequestMapping(value = "/heatmap", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    @Override
    public String getHeatMapPage(Model model){
        return "heatmap";
    }

    @RequestMapping(value = "/video/{cameraId}", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    @Override
    public String getVideoPage(@PathVariable(name = "cameraId") String cameraId, Model model){
        return "video";
    }
}
