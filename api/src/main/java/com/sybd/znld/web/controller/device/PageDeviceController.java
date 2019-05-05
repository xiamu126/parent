package com.sybd.znld.web.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.web.controller.device.BaseDeviceController;
import com.sybd.znld.web.controller.device.IPageDeviceController;
import com.sybd.znld.web.onenet.IOneNetService;
import com.sybd.znld.web.onenet.dto.GetLastDataStreamsResult;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class PageDeviceController extends BaseDeviceController implements IPageDeviceController {

    private final Logger log = LoggerFactory.getLogger(PageDeviceController.class);

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
            List<GetLastDataStreamsResult.DataStream> dataStreams = ret.getData().getDevices().get(0).getDataStreams();
            GetLastDataStreamsResult.DataStream weidu = dataStreams.stream()
                    .filter(dataStream -> dataStream.getId().equals("3336_0_5513"))
                    .findFirst().orElse(null);
            GetLastDataStreamsResult.DataStream jingdu = dataStreams.stream()
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
