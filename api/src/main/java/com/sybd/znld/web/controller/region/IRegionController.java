package com.sybd.znld.web.controller.region;

import com.sybd.znld.model.lamp.dto.Lamp;
import com.sybd.znld.model.lamp.dto.LampWithLocation;
import com.sybd.znld.web.controller.region.dto.RegionResult;
import com.sybd.znld.web.controller.region.dto.RegionTreeResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IRegionController {
    RegionResult getAllRegionWithValidLamp(String organId, HttpServletRequest request);
    @GetMapping(value="tree/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    RegionTreeResult getRegionTreeByOrganId(@PathVariable(name = "organId")String organId);
    @GetMapping(value="environment/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    List<LampWithLocation> getRegionOfEnvironmentList(@PathVariable(name = "organId")String organId);
}
