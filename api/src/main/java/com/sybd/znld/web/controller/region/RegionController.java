package com.sybd.znld.web.controller.region;

import com.sybd.znld.mapper.lamp.DataLocationMapper;
import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.model.lamp.dto.Lamp;
import com.sybd.znld.model.lamp.dto.LampWithLocation;
import com.sybd.znld.service.lamp.IRegionService;
import com.sybd.znld.service.rbac.IRbacService;
import com.sybd.znld.util.MyString;
import com.sybd.znld.web.controller.region.dto.RegionResult;
import com.sybd.znld.web.controller.region.dto.RegionTreeResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "区域接口")
@RestController
@RequestMapping("/api/v1/region")
public class RegionController implements IRegionController {
    private final IRegionService regionService;
    private final IRbacService rbacService;
    private final RegionMapper regionMapper;
    private final DataLocationMapper dataLocationMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public RegionController(IRegionService regionService, IRbacService rbacService,
                            RegionMapper regionMapper,
                            DataLocationMapper dataLocationMapper) {
        this.regionService = regionService;
        this.rbacService = rbacService;
        this.regionMapper = regionMapper;
        this.dataLocationMapper = dataLocationMapper;
    }

    @ApiOperation(value = "获取所有有效区域")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organId", value = "组织Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value="{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public RegionResult getAllRegionWithValidLamp(@PathVariable(name = "organId")String organId, HttpServletRequest request) {
        if(!MyString.isUuid(organId)){
            return RegionResult.fail("非法的参数");
        }
        var organ = this.rbacService.getOrganizationById(organId);
        if(organ == null){
            return RegionResult.fail("指定的组织不存在");
        }
        var ret = this.regionService.getAllRegionWithValidLamp(organId);
        return RegionResult.success(ret);
    }

    @Override
    public RegionTreeResult getRegionTreeByOrganId(String organId) {
        if(!MyString.isUuid(organId)){
            return RegionTreeResult.fail("非法的参数");
        }
        var organ = this.rbacService.getOrganizationById(organId);
        if(organ == null){
            return RegionTreeResult.fail("指定的组织不存在");
        }
        var ret = this.regionService.getRegionTreeByOrganId(organId);
        return RegionTreeResult.success(ret);
    }

    @Override
    public List<LampWithLocation> getRegionOfEnvironmentList(String organId) {
        var lamps = this.regionMapper.selectLampsOfEnvironment(organId);
        var result = new ArrayList<LampWithLocation>();
        var lampWithLocation = new LampWithLocation();
        for(var l : lamps){
            lampWithLocation.deviceId = l.deviceId;
            lampWithLocation.deviceName = l.deviceName;
            var jingdu = this.dataLocationMapper.selectByDeviceIdAndResourceName(l.deviceId, "北斗经度");
            var weidu = this.dataLocationMapper.selectByDeviceIdAndResourceName(l.deviceId, "北斗纬度");
            lampWithLocation.longitude = jingdu == null ? "" : jingdu.value.toString();
            lampWithLocation.latitude = weidu == null ? "" : weidu.value.toString();

            result.add(lampWithLocation);
        }
        return result;
    }
}
