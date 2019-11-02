package com.sybd.znld.account.controller.security;

import com.sybd.znld.account.controller.security.dto.NowAndKey;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.util.MyDateTime;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "安全接口")
@RestController
@RequestMapping("/api/v2/user/security")
public class SecurityController {
    private final RedissonClient redissonClient;
    private final RLock locker;

    @Autowired
    public SecurityController(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        var lockerName = this.getClass().getName()+".LOCKER";
        this.locker = this.redissonClient.getLock(lockerName);
    }

    @ApiOperation(value = "获取服务器当前时间戳与密钥")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public NowAndKey getNow(HttpServletRequest request){
        var nowAndKey = new NowAndKey();
        var now = MyDateTime.toTimestamp(LocalDateTime.now());
        var uuid = UUID.randomUUID().toString().replace("-", "");
        nowAndKey.now = now;
        nowAndKey.key = uuid;
        this.redissonClient.getBucket(uuid).set(now, 1, TimeUnit.DAYS); // 十五分钟后失效
        return nowAndKey;
    }

    //@PostMapping(value = "check", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public BaseApiResult verify(@RequestHeader("key") String theNow, HttpServletRequest request){
        var result = new BaseApiResult();
        if(this.redissonClient.getBucket(theNow).isExists()){
            result.code = 0;
            return result;
        }
        result.code = 1;
        return result;
    }
}
