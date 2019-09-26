package com.sybd.znld.web.controller;

import com.sybd.znld.model.ApiResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cache")
public class CacheController {
    @PostMapping(value = "evict", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult removeCache(){
        return null;
    }
}
