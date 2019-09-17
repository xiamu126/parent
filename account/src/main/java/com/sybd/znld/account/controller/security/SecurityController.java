package com.sybd.znld.account.controller.security;

import com.sybd.znld.account.controller.security.dto.NowAndKey;
import com.sybd.znld.util.MyDateTime;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "安全接口")
@RestController
@RequestMapping("/api/v2/security")
public class SecurityController {
    private final RedissonClient redissonClient;

    @Autowired
    public SecurityController(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @ApiOperation(value = "获取服务器当前时间戳与密钥")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public NowAndKey getNow(HttpServletRequest request){
        var now = MyDateTime.toTimestamp(LocalDateTime.now());
        var uuid = UUID.randomUUID().toString().replace("-", "");
        redissonClient.getBucket(uuid).set(now, 1, TimeUnit.HOURS); // 十五分钟后失效
        var nowAndKey = new NowAndKey();
        nowAndKey.now = now;
        nowAndKey.key = uuid;
        return nowAndKey;
    }
}
