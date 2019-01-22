package com.sybd.znld.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.service.ExecuteCommandService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;

@Slf4j
@Controller
public class PageDeviceController extends BaseDeviceController implements IPageDeviceController {

    @Autowired
    public PageDeviceController(RedisTemplate<String, Object> redisTemplate,
                                OneNetService oneNet,
                                ExecuteCommandService executeCommandService,
                                ProjectConfig projectConfig) {
        super(redisTemplate, oneNet, executeCommandService, projectConfig);
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
        if(ret.getErrno() == 0){
            var dataStreams = ret.getData().getDevices()[0].getDatastreams();
            var weidu = Arrays.stream(dataStreams)
                    .filter(dataStream -> dataStream.getId().equals("3336_0_5513"))
                    .findFirst().orElse(null);
            var jingdu = Arrays.stream(dataStreams)
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
