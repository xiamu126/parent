package com.sybd.znld.account.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import com.sybd.znld.account.config.ProjectConfig;
import com.sybd.znld.account.controller.user.dto.AccessToken;
import com.sybd.znld.account.controller.user.dto.LoginResult;
import com.sybd.znld.account.controller.user.dto.NeedCaptchaResult;
import com.sybd.znld.account.model.LoginInput;
import com.sybd.znld.account.service.IUserService;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import com.sybd.znld.util.*;
import com.wf.captcha.SpecCaptcha;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "用户接口")
@RestController
@RequestMapping("/api/v2/user")
public class UserController implements IUserController {
    private final IUserService userService;
    private final RedissonClient redissonClient;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    private final MongoClient mongoClient;
    private final ProjectConfig projectConfig;
    //private final ISigService sigService;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;
    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;
    @Value("${security.oauth2.client.access-token-uri}")
    private String accessTokenUri;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${project.captcha.expiration-time}")
    private Duration captchaExpirationTime;
    @Value("${project.captcha.width}")
    private Integer width;
    @Value("${project.captcha.height}")
    private Integer height;
    @Value("${project.captcha.length}")
    private Integer length;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public UserController(IUserService userService,
                          RedissonClient redissonClient, MongoClient mongoClient, ProjectConfig projectConfig) {
        this.userService = userService;
        this.redissonClient = redissonClient;
        this.mongoClient = mongoClient;
        this.projectConfig = projectConfig;
    }

    @ApiOperation(value = "获取验证码")
    @GetMapping(value = "login/captcha", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public String getCaptcha(HttpServletRequest request) {
        var specCaptcha = new SpecCaptcha(this.width, this.height, this.length);
        var verCode = specCaptcha.text().toLowerCase();
        while (redissonClient.getBucket(verCode).isExists()) {
            verCode = specCaptcha.text().toLowerCase();
        }
        var tmp = redissonClient.getBucket(verCode);
        var captchaExpirationTime = this.captchaExpirationTime.toSeconds();
        tmp.set("", captchaExpirationTime, TimeUnit.SECONDS);
        return specCaptcha.toBase64();
    }

    @ApiOperation(value = "登入")
    @PostMapping(value = "login2", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult login2(@ApiParam(name = "jsonData", value = "登入数据", required = true) @RequestBody @Valid LoginInput input,
                            @RequestHeader("now") Long now,
                            @RequestHeader("nonce") String nonce,
                            @RequestHeader("sig") String sig,
                            @RequestHeader("key") String secretKey,
                            HttpServletRequest request, BindingResult bindingResult) {
        try {
            /*if(input == null || MyString.isEmptyOrNull(nonce) || MyString.isEmptyOrNull(sig)){
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
            var theSig = MySignature.generate(input.toMap(), secretKey);
            log.debug(theSig);
            if(!theSig.equals(sig)){
                log.debug("签名错误");
                return ApiResult.fail("签名错误");
            }*/
            //var ret = this.sigService.checkSig(input.toMap(), secretKey, now,nonce,sig);
            //if(ret.isOk()) return login(input, request, bindingResult);
            //return ret;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return ApiResult.fail("用户名或密码错误");
    }

    @ApiOperation(value = "登入")
    @PostMapping(value = "login", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult login(@ApiParam(name = "jsonData", value = "登入数据", required = true) @RequestBody @Valid LoginInput input,
                           HttpServletRequest request, BindingResult bindingResult) {
        try {
            if (input == null) {
                log.debug("传入的参数错误");
                return ApiResult.fail("用户名或密码错误");
            }
            var userIp = MyIp.getIpAddr(request);
            var count = (Integer) this.redissonClient.getBucket(userIp).get();
            if (count != null && count >= 3) { // 这个ip被登记过，且已经达到错误上线，必须验证验证码
                if (MyString.isEmptyOrNull(input.captcha) || !this.redissonClient.getBucket(input.captcha.toLowerCase()).isExists()) {
                    var loginResult = new NeedCaptchaResult();
                    loginResult.needCaptcha = true;
                    log.debug("累次错误已经达到3次，但验证码为空，或找不到验证码的缓存");
                    return ApiResult.fail("验证码错误或已失效", loginResult);
                } else {
                    this.redissonClient.getBucket(input.captcha.toLowerCase()).delete(); // 验证通过，删除验证码
                }
            } else { // 如果没有达到错误上限；但是客户端传来了验证码，不管是否存在错误记录都会验证
                log.debug("累计错误未达到上限");
                if (input.captcha != null && !this.redissonClient.getBucket(input.captcha.toLowerCase()).isExists()) {
                    log.debug("传来了验证码，但找不到验证码的缓存");
                    return ApiResult.fail("验证码错误或已失效");
                }
            }
            var user = this.userService.getUserByName(input.user);
            if (user != null) {
                if (!this.encoder.matches(input.password, user.password)) {
                    if (count == null) {
                        count = 1;
                    } else {
                        count = count + 1;
                    }
                    log.debug("用户ip为：" + userIp + ";累计：" + count);
                    // 如果之前已经记录过这个错误IP，则重置过期时间
                    this.redissonClient.getBucket(userIp).set(count, 1, TimeUnit.DAYS);
                    if (count >= 3) {
                        var loginResult = new NeedCaptchaResult();
                        loginResult.needCaptcha = true;
                        return ApiResult.fail("用户名或密码错误", loginResult);
                    } else {
                        return ApiResult.fail("用户名或密码错误");
                    }
                }
                var db = mongoClient.getDatabase("test");
                var c1 = db.getCollection("com.sybd.znld.account.profile");
                var myDoc = c1.find(Filters.eq("id", user.id)).first();
                String jsonStr = null;
                if (myDoc != null) {
                    myDoc.remove("_id");
                    jsonStr = myDoc.toJson();
                }
                // 获取rbac权限信息
                var headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                var map = new LinkedMultiValueMap<String, String>();
                map.add("grant_type", "password");
                map.add("client_id", this.clientId);
                map.add("client_secret", this.clientSecret);
                map.add("username", input.user);
                map.add("password", input.password);
                var httpEntity = new HttpEntity<>(map, headers);
                var token = this.restTemplate.exchange(this.accessTokenUri, HttpMethod.POST, httpEntity, AccessToken.class);
                var body = token.getBody();
                if (body == null) {
                    return ApiResult.fail("此账号无权限");
                }
                var data = new LoginResult();
                data.token = body.access_token;
                data.tokenExpire = MyDateTime.toTimestamp(LocalDateTime.now(), body.expires_in);
                data.userId = user.id;
                data.organId = user.organizationId;
                data.menu = jsonStr;
                if (input.user.equals("sybd_test_admin") || input.user.equals("sybd_test_user")) {
                    data.isRoot = true;
                }

                return ApiResult.success(data);
            } else { // 这是用户名错误，也算累计
                if (count == null) {
                    count = 1;
                } else {
                    count = count + 1;
                }
                log.debug("用户ip为：" + userIp + ";累计：" + count);
                this.redissonClient.getBucket(userIp).set(count, 1, TimeUnit.DAYS); // 延长错误记录的时间
                if (count >= 3) {
                    var loginResult = new NeedCaptchaResult();
                    loginResult.needCaptcha = true;
                    return ApiResult.fail("用户名或密码错误", loginResult);
                } else {
                    return ApiResult.fail("用户名或密码错误");
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return ApiResult.fail("用户名或密码错误");
    }

    @ApiOperation(value = "登出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "string", paramType = "path")
    })
    @PostMapping(value = "logout", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public ApiResult logout(@RequestBody String jsonStr, HttpServletRequest request) {
        if (MyString.isEmptyOrNull(jsonStr)) return ApiResult.fail("错误的用户信息");
        String id = JsonPath.read(jsonStr, "$.id");
        return ApiResult.success();
    }

    @PostMapping(value = "register", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public ApiResult register(@RequestBody @Valid RegisterInput input, HttpServletRequest request, BindingResult bindingResult) {
        try {
            if (userService.register(input) != null) {
                return ApiResult.success();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return ApiResult.fail("注册失败");
    }

    @GetMapping(value = "{id:[0-9a-f]{32}}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public ApiResult getUserInfo(@PathVariable(name = "id") String id, HttpServletRequest request) {
        var tmp = this.userService.getUserById(id);
        return ApiResult.success(tmp);
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public ApiResult updateUserInfo(@RequestBody @Valid UserModel input, HttpServletRequest request, BindingResult bindingResult) {
        this.userService.modifyUserById(input);
        return ApiResult.success();
    }
}
