package com.sybd.znld.account.controller.user;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import com.sybd.znld.account.controller.user.dto.LoginResult;
import com.sybd.znld.account.controller.user.dto.LogoutResult;
import com.sybd.znld.account.model.LoginInput;
import com.sybd.znld.account.service.IUserService;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import com.sybd.znld.util.MyString;
import com.wf.captcha.SpecCaptcha;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    private final int CAPTCHA_EXPIRATION_TIME = 30;
    private final int AUTH2_TOKEN_EXPIRATION_TIME = 7*24*60*60;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;
    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Autowired
    public UserController(IUserService userService,
                          RedissonClient redissonClient, MongoClient mongoClient) {
        this.userService = userService;
        this.redissonClient = redissonClient;
        this.mongoClient = mongoClient;
    }

    private String getCaptchaKey(String uuid){
        return "captcha::"+ uuid;
    }

    @ApiOperation(value = "获取验证码图片")
    @GetMapping(value = "login/captcha", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public String getCaptcha(HttpServletRequest request){
        var specCaptcha = new SpecCaptcha(130, 48, 5);
        var verCode = specCaptcha.text().toLowerCase();
        while(redissonClient.getBucket(verCode).isExists()){
            verCode = specCaptcha.text().toLowerCase();
        }
        // 这个uuid是页面传来的，主要是为了方便用作缓存的索引
        redissonClient.getBucket(verCode).set("", CAPTCHA_EXPIRATION_TIME, TimeUnit.SECONDS);
        return specCaptcha.toBase64();
    }

    @ApiOperation(value = "登入")
    @PostMapping(value = "login", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public LoginResult login(@ApiParam(name = "jsonData", value = "登入数据", required = true) @RequestBody @Valid LoginInput input,
                             HttpServletRequest request, BindingResult bindingResult){
        if(!redissonClient.getBucket(input.captcha).isExists()){
            return LoginResult.fail("验证码错误或已失效");
        }
        try {
            var user = this.userService.getUserByName(input.user);
            if(user != null){
                if(!this.encoder.matches(input.password, user.password)) {
                    return LoginResult.fail("用户名或密码错误");
                }
                redissonClient.getBucket(input.captcha).delete();
                var db = mongoClient.getDatabase( "test" );
                var c1 = db.getCollection("com.sybd.znld.account.profile");
                var myDoc = c1.find(Filters.eq("id", user.id)).first();
                var jsonStr = "";
                if( myDoc != null) {
                    myDoc.remove("_id");
                    jsonStr = myDoc.toJson();
                }
                // 获取rbac权限信息
                return LoginResult.success(user.id, user.organizationId, clientId, clientSecret, (long) AUTH2_TOKEN_EXPIRATION_TIME, jsonStr);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return LoginResult.fail("用户名或密码错误");
    }

    @PostMapping(value = "login/captcha/{captcha:[0-9a-f]{32}}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public ApiResult verifyCaptcha(@PathVariable(name = "captcha") String captcha, HttpServletRequest request) {
        if(MyString.isEmptyOrNull(captcha) || captcha.length() < 4) return ApiResult.fail("验证码错误");

        var rightCaptcha = request.getSession().getAttribute("captcha").toString();
        if(captcha.equalsIgnoreCase(rightCaptcha)){
            request.getSession().removeAttribute("captcha");
            return ApiResult.success();
        }
        return ApiResult.fail("验证码错误");
    }

    @ApiOperation(value = "登出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "logout/{userId}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public LogoutResult logout(@PathVariable(name = "userId") String id, HttpServletRequest request){
        if(MyString.isEmptyOrNull(id)) return LogoutResult.fail("登出失败");
        request.getSession().removeAttribute("user");
        return LogoutResult.success();
    }

    @PostMapping(value = "register", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public ApiResult register(@RequestBody @Valid RegisterInput input, HttpServletRequest request, BindingResult bindingResult){
        try {
            //input.password = this.encoder.encode(input.password);
            if(userService.register(input) != null){
                return ApiResult.success();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return ApiResult.fail("注册失败");
    }

    @GetMapping(value = "{id:[0-9a-f]{32}}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public ApiResult getUserInfo(@PathVariable(name = "id") String id, HttpServletRequest request){
        var tmp = this.userService.getUserById(id);
        return ApiResult.success(tmp);
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public ApiResult updateUserInfo(@RequestBody @Valid UserModel input, HttpServletRequest request, BindingResult bindingResult){
        this.userService.modifyUserById(input);
        return ApiResult.success();
    }
}
