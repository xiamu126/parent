package com.sybd.znld.account.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import com.sybd.znld.account.config.ProjectConfig;
import com.sybd.znld.account.controller.user.dto.AccessToken;
import com.sybd.znld.mapper.rbac.*;
import com.sybd.znld.model.rbac.dto.*;
import com.sybd.znld.account.controller.user.dto.LoginResult;
import com.sybd.znld.account.controller.user.dto.NeedCaptchaResult;
import com.sybd.znld.account.service.IUserService;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.util.*;
import com.wf.captcha.SpecCaptcha;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class UserController implements IUserController {
    private final IUserService userService;
    private final RedissonClient redissonClient;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    private final MongoClient mongoClient;
    private final ProjectConfig projectConfig;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;
    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;
    @Value("${security.oauth2.client.access-token-uri}")
    private String accessTokenUri;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OrganizationMapper organizationMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RoleAuthorityGroupMapper roleAuthorityGroupMapper;
    private final AuthorityGroupMapper authorityGroupMapper;
    private final AuthorityMapper authorityMapper;

    @Value("${project.captcha.expiration-time}")
    private Duration captchaExpirationTime;
    @Value("${project.captcha.width}")
    private Integer width;
    @Value("${project.captcha.height}")
    private Integer height;
    @Value("${project.captcha.length}")
    private Integer length;
    @Value("${app.login-success-expiration-time}")
    private Duration loginSuccessExpirationTime;
    @Value("${app.login-error-expiration-time}")
    private Duration loginErrorExpirationTime;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public UserController(IUserService userService,
                          RedissonClient redissonClient,
                          MongoClient mongoClient,
                          ProjectConfig projectConfig,
                          RestTemplate restTemplate,
                          ObjectMapper objectMapper,
                          OrganizationMapper organizationMapper,
                          UserMapper userMapper,
                          UserRoleMapper userRoleMapper,
                          RoleMapper roleMapper,
                          RoleAuthorityGroupMapper roleAuthorityGroupMapper,
                          AuthorityGroupMapper authorityGroupMapper,
                          AuthorityMapper authorityMapper) {
        this.userService = userService;
        this.redissonClient = redissonClient;
        this.mongoClient = mongoClient;
        this.projectConfig = projectConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.organizationMapper = organizationMapper;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.roleAuthorityGroupMapper = roleAuthorityGroupMapper;
        this.authorityGroupMapper = authorityGroupMapper;
        this.authorityMapper = authorityMapper;
    }

    @Override
    public String getCaptcha(HttpServletRequest request) {
        var specCaptcha = new SpecCaptcha(this.width, this.height, this.length);
        var verCode = specCaptcha.text().toLowerCase();
        while (redissonClient.getBucket(verCode).isExists()) {
            verCode = specCaptcha.text().toLowerCase();
        }
        var tmp = redissonClient.getBucket(verCode);
        tmp.set("", this.captchaExpirationTime.toSeconds(), TimeUnit.SECONDS);
        log.debug(verCode);
        return specCaptcha.toBase64();
    }

    @ApiOperation(value = "登入")
    @PostMapping(value = "login2", produces = {MediaType.APPLICATION_JSON_VALUE})
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

    private List<RbacApiInfoSummary> getRbacApiInfoByUserId(String userId, String app) {
        List<RbacApiInfoSummary> results = new ArrayList<>();
        var userRoleModels = this.userRoleMapper.selectByUserId(userId);
        // 根据角色获取权限，一个账号可以关联多个角色
        /*userRoleModels.forEach(userRoleModel -> {
            var roleAuthModels = this.roleAuthorityGroupMapper.selectByRoleId(userRoleModel.roleId);
            // 一个角色可以关联多个权限组
            roleAuthModels.forEach(roleAuthModel -> {
                var authorities = this.authorityMapper.selectByAuthGroupIdAndAppAndType(roleAuthModel.authGroupId, app, RbacInfo.Type.API.getValue());
                authorities.forEach(a -> {
                    List<RbacApiInfo.Detail> exclude = null;
                    try {
                        exclude = JsonPath.read(a.uri,"exclude");
                    }catch (PathNotFoundException ignored){ }
                    List<RbacApiInfo.Detail> include = null;
                    try {
                        include = JsonPath.read(a.uri,"include");
                    }catch (PathNotFoundException ignored){ }
                    var summary = new RbacApiInfoSummary();
                    summary.exclude = exclude;
                    summary.include = include;
                    results.add(summary);
                });
            });
        });*/
        return results;
    }

    private List<RbacWebInfoSummary> getRbacWebInfoByUserId(String userId, String app) {
        List<RbacWebInfoSummary> results = new ArrayList<>();
        var userRoleModels = this.userRoleMapper.selectByUserId(userId);
        // 根据角色获取权限，一个账号可以关联多个角色
        /*userRoleModels.forEach(userRoleModel -> {
            var roleAuthModels = this.roleAuthorityGroupMapper.selectByRoleId(userRoleModel.roleId);
            // 一个角色可以关联多个权限组
            roleAuthModels.forEach(roleAuthModel -> {
                var authorities = this.authorityMapper.selectByAuthGroupIdAndAppAndType(roleAuthModel.authGroupId, app, RbacInfo.Type.WEB.getValue());
                authorities.forEach(a -> {
                    List<RbacWebInfo.Detail> exclude = null;
                    try {
                        exclude = JsonPath.read(a.uri,"exclude");
                    }catch (PathNotFoundException ignored){ }
                    List<RbacWebInfo.Detail> include = null;
                    try {
                        include = JsonPath.read(a.uri,"include");
                    }catch (PathNotFoundException ignored){ }
                    var summary = new RbacWebInfoSummary();
                    summary.exclude = exclude;
                    summary.include = include;
                    results.add(summary);
                });
            });
        });*/
        return results;
    }

    public ApiResult loginV2(LoginInput input, HttpServletRequest request, BindingResult bindingResult) {
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
            var user = this.userMapper.selectByName(input.user);
            if (user != null) {
                if (!this.encoder.matches(input.password, user.password)) {
                    if (count == null) {
                        count = 1;
                    } else {
                        count = count + 1;
                    }
                    log.debug("用户ip为：" + userIp + ";累计：" + count);
                    // 如果之前已经记录过这个错误IP，则重置过期时间
                    this.redissonClient.getBucket(userIp).set(count, this.loginErrorExpirationTime.toSeconds(), TimeUnit.SECONDS);
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
                var expireSeconds = body.expires_in - 10 * 60;
                data.tokenExpire = MyDateTime.toTimestamp(LocalDateTime.now(), expireSeconds > 0 ? expireSeconds : body.expires_in);
                data.userId = user.id;
                data.organId = user.organizationId;
                data.menu = jsonStr;
                data.rbac = this.getRbacWebInfoByUserId(user.id, "znld");
                if (input.user.equals("sybd_test_admin")) {
                    data.isRoot = true;
                    data.menu = null;
                    data.cities = this.organizationMapper.selectAllCityAndCode();
                    if(data.cities != null && !data.cities.isEmpty()){
                        for(var c : data.cities){
                            var users = this.userMapper.selectByOrganizationId(c.code);
                            if(users != null && !users.isEmpty()){
                                db = mongoClient.getDatabase("test");
                                c1 = db.getCollection("com.sybd.znld.account.profile");
                                myDoc = c1.find(Filters.eq("id", users.get(0).id)).first();
                                if (myDoc != null) {
                                    myDoc.remove("_id");
                                    myDoc.remove("app");
                                    myDoc.remove("device");
                                    jsonStr = myDoc.toJson();
                                }
                            }
                            c.menu = jsonStr;
                            jsonStr = null;
                        }
                    }
                }
                user.lastLoginTime = LocalDateTime.now();
                user.lastLoginIp = userIp;
                this.userMapper.updateById(user);
                // 成功登入后，保存登入信息
                data.id = UUID.randomUUID().toString().replace("-","");
                var bucket = this.redissonClient.getBucket(data.id);
                bucket.set(data, this.loginSuccessExpirationTime.toSeconds(), TimeUnit.SECONDS);
                return ApiResult.success(data);
            } else { // 这是用户名错误，也算累计
                if (count == null) {
                    count = 1;
                } else {
                    count = count + 1;
                }
                log.debug("用户ip为：" + userIp + ";累计：" + count);
                this.redissonClient.getBucket(userIp).set(count, this.loginErrorExpirationTime.toSeconds(), TimeUnit.SECONDS); // 延长错误记录的时间
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
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return ApiResult.fail("用户名或密码错误");
    }

    @Override
    public ApiResult loginV3(LoginInput input, HttpServletRequest request, BindingResult bindingResult) {
        if(input == null || MyString.isEmptyOrNull(input.user) || MyString.isEmptyOrNull(input.password)) {
            return ApiResult.fail("参数错误");
        }
        return this.userService.loginV3(input, request);
    }

    @GetMapping(value = "menu/{code:[0-9a-f]{32}}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public String getMenu(@PathVariable(name = "code") String code, @RequestHeader("root") String rootId, HttpServletRequest request){
        var user = this.userMapper.selectById(rootId);
        if(user == null){
            return null;
        }
        var users = this.userMapper.selectByOrganizationId(code);
        String jsonStr = null;
        if(users != null && !users.isEmpty()){
            var db = mongoClient.getDatabase("test");
            var c1 = db.getCollection("com.sybd.znld.account.profile");
            var myDoc = c1.find(Filters.eq("id", users.get(0).id)).first();
            if (myDoc != null) {
                myDoc.remove("_id");
                jsonStr = myDoc.toJson();
            }
        }
        return jsonStr;
    }


    @Override
    public ApiResult logoutV2(String jsonStr, HttpServletRequest request) {
        if (MyString.isEmptyOrNull(jsonStr)) return ApiResult.fail("错误的用户信息");
        String id = JsonPath.read(jsonStr, "$.id");
        return ApiResult.success();
    }

    @Override
    public ApiResult logoutV3(LogoutInput input, HttpServletRequest request) {
        if(input == null || MyString.isEmptyOrNull(input.id)) {
            log.error("参数错误");
            return ApiResult.fail("参数错误");
        }
        return this.userService.logoutV3(input.id);
    }

    @Override
    public ApiResult register(RegisterInput input, HttpServletRequest request, BindingResult bindingResult) {
        try {
            if (userService.register(input) != null) {
                return ApiResult.success();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return ApiResult.fail("注册失败");
    }
}
