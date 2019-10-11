package com.sybd.znld.rpc.service;

import com.sybd.znld.model.ApiResult;
import com.sybd.znld.service.ISigService;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MySignature;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SigService implements ISigService {
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public ApiResult checkSig(Map<String, String> params, String secretKey, Long now, String nonce, String sig) {
        try{
            if(params == null || params.isEmpty() || MyString.isEmptyOrNull(nonce) || MyString.isEmptyOrNull(sig)){
                log.debug("传入的参数错误");
                return ApiResult.fail("参数错误");
            }
            var theNow = MyDateTime.toLocalDateTime(now);
            if(theNow == null){
                log.debug("传入的时间戳错误");
                return ApiResult.fail("参数错误");
            }
            var d = Duration.between(LocalDateTime.now(), theNow);
            if(Math.abs(d.toSeconds()) >= 30){ // 超过30秒
                log.debug("时差超过指定值");
                return ApiResult.fail("参数错误");
            }
            if(!this.redissonClient.getBucket(secretKey).isExists()){
                log.debug("secretKey不存在");
                return ApiResult.fail("非法key");
            }
            if(this.redissonClient.getBucket(nonce).isExists()){
                log.debug("nonce存在，重复的请求");
                return ApiResult.fail("重复请求");
            }else {
                this.redissonClient.getBucket(nonce).set("", 3, TimeUnit.MINUTES);
            }
            var theSig = MySignature.generate(params, secretKey);
            log.debug(theSig);
            if(!theSig.equals(sig)){
                log.debug("签名错误");
                return ApiResult.fail("签名错误");
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
            ex.printStackTrace();
            return ApiResult.fail("发生错误");
        }
        return ApiResult.success();
    }
}
