package com.sybd.znld.ministar.controller;

import com.sybd.znld.ministar.model.SubtitleForRegion;
import com.sybd.znld.ministar.model.SubtitleForRegionPrepare;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.dto.MiniStarEffect;
import com.sybd.znld.model.lamp.dto.MiniStarEffectForSave;
import com.sybd.znld.model.lamp.dto.RegionWithLamps;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface IMiniStarController {

    @ApiOperation(value = "预存灯带节目，针对区域街道")
    @PostMapping(value="region/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    ApiResult storeTask(@PathVariable(name = "organId")String organId,
                        @ApiParam(value = "具体的指令集", required = true) @RequestBody SubtitleForRegionPrepare subtitles, HttpServletRequest request);

    @ApiOperation(value = "新建灯带效果，针对区域街道的")
    @PostMapping(value = "region", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    BaseApiResult newMiniStar(@ApiParam(value = "具体的指令集", required = true) @RequestBody SubtitleForRegion subtitle, HttpServletRequest request);

    @ApiOperation(value = "获取所有有效区域")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organId", value = "组织Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value="tree/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    List<RegionWithLamps> getAllRegionWithValidLamp(@PathVariable(name = "organId")String organId, HttpServletRequest request);

    @ApiOperation(value = "新建效果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organId", value = "组织Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "effects", value = "新建效果集", required = true, dataType = "list", paramType = "body")
    })
    @PostMapping(value="effect/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    ApiResult saveEffects(@PathVariable(name = "organId")String organId,
                          @RequestBody List<MiniStarEffectForSave> effects, HttpServletRequest request);

    @ApiOperation(value = "修改效果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organId", value = "组织Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "effects", value = "新建效果集", required = true, dataType = "list", paramType = "body")
    })
    @PutMapping(value="effect/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    ApiResult modifyEffects(@PathVariable(name = "organId")String organId,
                              @RequestBody List<MiniStarEffect> effects, HttpServletRequest request);

    @ApiOperation(value = "获取效果列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organId", value = "组织Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value="effect/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    ApiResult getEffects(@PathVariable(name = "organId")String organId);

    @ApiOperation(value = "移除效果")
    @DeleteMapping(value="effect/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    ApiResult removeEffects(@PathVariable(name = "organId")String organId, @RequestBody Map<String, List<Integer>> data, HttpServletRequest request);
}
