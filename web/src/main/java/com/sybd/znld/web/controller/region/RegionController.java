package com.sybd.znld.web.controller.region;

import com.sybd.znld.web.controller.region.dto.RegionResult;
import com.sybd.znld.web.service.rbac.IRbacService;
import com.sybd.znld.web.service.znld.IRegionService;
import com.sybd.znld.znld.util.MyString;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Api(tags = "区域接口")
@RestController
@RequestMapping("/api/v1/region")
public class RegionController implements IRegionController {
    private final IRegionService regionService;
    private final IRbacService rbacService;

    @Autowired
    public RegionController(IRegionService regionService, IRbacService rbacService) {
        this.regionService = regionService;
        this.rbacService = rbacService;
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
}
