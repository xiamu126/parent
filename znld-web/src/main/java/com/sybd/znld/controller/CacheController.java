package com.sybd.znld.controller;

import com.sybd.znld.core.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "缓存接口")
@RestController
@RequestMapping("/api/v1/cache")
public class CacheController {
    private final Logger log = LoggerFactory.getLogger(CacheController.class);

    @ApiOperation(value = "失效某一缓存")
    @PostMapping(value = "evict", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult removeCache(){
        return null;
    }
}