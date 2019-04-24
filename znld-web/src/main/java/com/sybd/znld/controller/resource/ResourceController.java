package com.sybd.znld.controller.resource;

import com.sybd.znld.controller.resource.dto.CheckedResourcesResult;
import com.sybd.znld.onenet.IOneNetService;
import com.sybd.znld.service.znld.ILampService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Api(tags = "资源接口")
@RestController
@RequestMapping("/api/v1/resource")
public class ResourceController implements IResourceController {
    private final IOneNetService oneNetService;
    private final ILampService lampService;

    @Autowired
    public ResourceController(IOneNetService oneNetService, ILampService lampService) {
        this.oneNetService = oneNetService;
        this.lampService = lampService;
    }

    @ApiOperation(value = "获取某个设备的所有可用的资源Id和名字，通过设备Id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备的Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "{deviceId:^[1-9]\\d*$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public CheckedResourcesResult getCheckedResources(@PathVariable("deviceId") Integer deviceId, HttpServletRequest request) {
        try{
            var ret = this.lampService.getCheckedResourceByDeviceId(deviceId);
            if(ret == null || ret.isEmpty()){
                return CheckedResourcesResult.fail("获取数据为空");
            }
            return CheckedResourcesResult.success(ret);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return CheckedResourcesResult.fail("获取数据失败");
    }

    @ApiOperation(value = "获取某个设备的所有可用的资源Id和名字，通过组织Id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organId", value = "组织Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "{organId:[0-9a-f]{32}}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public CheckedResourcesResult getCheckedResources(@PathVariable("organId") String organId, HttpServletRequest request) {
        try{
            var ret = this.lampService.getCheckedResourceByOrganId(organId);
            if(ret == null || ret.isEmpty()){
                return CheckedResourcesResult.fail("获取数据为空");
            }
            return CheckedResourcesResult.success(ret);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return CheckedResourcesResult.fail("获取数据失败");
    }
}
