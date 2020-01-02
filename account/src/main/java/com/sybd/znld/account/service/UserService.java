package com.sybd.znld.account.service;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import com.sybd.znld.account.controller.user.dto.AccessToken;
import com.sybd.znld.account.controller.user.dto.NeedCaptchaResult;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.mapper.rbac.UserRoleMapper;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.rbac.AuthorityModel;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.UserStatus;
import com.sybd.znld.model.rbac.dto.LoginInput;
import com.sybd.znld.model.rbac.dto.LoginResult;
import com.sybd.znld.model.rbac.dto.LogoutResult;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyIp;
import com.sybd.znld.util.MyString;
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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserService implements IUserService {
    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;
    private final RegionMapper regionMapper;
    private final LampMapper lampMapper;
    private final LampResourceMapper lampResourceMapper;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final OrganizationMapper organizationMapper;
    private final LampRegionMapper lampRegionMapper;
    private final BCryptPasswordEncoder encoder;
    private final RedissonClient redissonClient;
    private final MongoClient mongoClient;
    private final RestTemplate restTemplate;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;
    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;
    @Value("${security.oauth2.client.access-token-uri}")
    private String accessTokenUri;
    @Value("${security.oauth2.client.token-check-uri}")
    private String tokenCheckUri;
    @Value("${security.oauth2.client.token-delete-uri}")
    private String tokenDeleteUri;
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
    @Value("${app.login-error-count}")
    private Integer loginErrorCount;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public UserService(UserMapper userMapper,
                       UserRoleMapper userRoleMapper,
                       RegionMapper regionMapper,
                       LampMapper lampMapper,
                       LampResourceMapper lampResourceMapper,
                       OneNetResourceMapper oneNetResourceMapper,
                       OrganizationMapper organizationMapper,
                       LampRegionMapper lampRegionMapper,
                       RedissonClient redissonClient,
                       MongoClient mongoClient,
                       RestTemplate restTemplate) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.regionMapper = regionMapper;
        this.lampMapper = lampMapper;
        this.lampResourceMapper = lampResourceMapper;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.organizationMapper = organizationMapper;
        this.lampRegionMapper = lampRegionMapper;
        this.redissonClient = redissonClient;
        this.mongoClient = mongoClient;
        this.restTemplate = restTemplate;
        this.encoder = new BCryptPasswordEncoder(10);
    }

    private boolean needCaptcha(HttpServletRequest request) {
        var userIp = MyIp.getIpAddr(request);
        if(userIp == null) {
            return true;
        }
        var count = (Integer) this.redissonClient.getBucket(userIp).get();
        if(count == null) {
            // 没有错误记录
            return false;
        }
        // 这个ip被登记过，且已经达到错误上线，必须验证验证码
        return count >= this.loginErrorCount;
    }

    private boolean updateLoginError(HttpServletRequest request) {
        var userIp = MyIp.getIpAddr(request);
        if(userIp == null) {
            return false;
        }
        var count = (Integer) this.redissonClient.getBucket(userIp).get();
        if(count == null) {
            // 第一次错误的时候，还没有缓存
            this.redissonClient.getBucket(userIp).set(1, this.loginErrorExpirationTime.toSeconds(), TimeUnit.SECONDS);
            log.debug("此ip["+userIp+"]当前累计错误次数为"+1);
        } else {
            // 否则在之前的基础上累计1
            count++;
            this.redissonClient.getBucket(userIp).set(count, this.loginErrorExpirationTime.toSeconds(), TimeUnit.SECONDS);
            log.debug("此ip["+userIp+"]当前累计错误次数为"+count);
        }
        return true;
    }

    @Override
    public ApiResult loginV3(LoginInput input, HttpServletRequest request) {
        try {
            if (input == null || !input.isValid()) {
                log.error("参数错误");
                return ApiResult.fail("参数错误");
            }
            if(this.needCaptcha(request)) {
                var loginResult = new LoginResult();
                loginResult.needCaptcha = true;
                if(MyString.isEmptyOrNull(input.captcha)) {
                    log.error("验证码为空");
                    return ApiResult.fail("请输入验证码", loginResult);
                } else {
                    if(this.redissonClient.getBucket(input.captcha.toLowerCase()).isExists()) {
                        this.redissonClient.getBucket(input.captcha.toLowerCase()).delete(); // 验证通过，删除验证码
                    } else {
                        return ApiResult.fail("验证码错误", loginResult);
                    }
                }
            } // 如果不需要验证码，而前端又传来了验证码，忽略

            var user = this.userMapper.selectByName(input.user);
            if (user != null) {
                if (!this.encoder.matches(input.password, user.password)) {
                    // 更新错误记录
                    if(!this.updateLoginError(request)) {
                        log.error("无法更新错误记录");
                        return ApiResult.fail("发生错误");
                    }
                    // 更新错误记录后，重新查看是否需要验证码
                    if(this.needCaptcha(request)) {
                        var loginResult = new LoginResult();
                        loginResult.needCaptcha = true;
                        return ApiResult.fail("用户名或密码错误", loginResult);
                    } else {
                        return ApiResult.fail("用户名或密码错误");
                    }
                }
                // 以下就是验证通过之后的逻辑
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
                user.lastLoginTime = LocalDateTime.now();
                user.lastLoginIp = MyIp.getIpAddr(request);
                this.userMapper.updateById(user);
                return ApiResult.success(data);
            } else {
                // 这是用户名错误，也算累计
                // 更新错误记录
                if(!this.updateLoginError(request)) {
                    log.error("无法更新错误记录");
                    return ApiResult.fail("发生错误");
                }
                // 更新错误记录后，重新查看是否需要验证码
                if(this.needCaptcha(request)) {
                    var loginResult = new LoginResult();
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
    public ApiResult logoutV3(String token) {
        if(MyString.isEmptyOrNull(token)) {
            return ApiResult.fail("参数错误");
        }
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer "+token);
        var httpEntity = new HttpEntity<>(headers);
        var result = this.restTemplate.exchange(this.tokenDeleteUri+"?access_token="+token, HttpMethod.DELETE, httpEntity, ApiResult.class);
        var body = result.getBody();
        return body == null ? ApiResult.fail() : body;
    }

    @Override
    //@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public UserModel addUser(UserModel model) {
        if(model == null || !model.isValidForInsert()) {
            return null;
        }
        //名字不能重复
        if(this.userMapper.selectByName(model.name) != null) {
            return null;
        }
        //所属组织必须存在
        if(this.organizationMapper.selectById(model.organizationId) == null) {
            return null;
        }
        model.password = this.encoder.encode(model.password); // 密码加密后存储，统一在这里做，其它地方可以免去
        if(this.userMapper.insert(model) > 0) {
            return model;
        } else {
            return null;
        }
    }

    @Override
    public UserModel register(RegisterInput input) {
        if(input == null || !input.isValid()) return null;
        var user = new UserModel();
        user.name = input.name;
        user.password = input.password;
        user.organizationId = input.organizationId;
        user.lastLoginTime = LocalDateTime.now();
        user.status = UserStatus.OK;
        return addUser(user);
    }
}
