package com.sybd.znld.controller.user;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.controller.user.dto.LoginResult;
import com.sybd.znld.controller.user.dto.LogoutResult;
import com.sybd.znld.core.ApiResult;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.service.rbac.dto.LoginInput;
import com.sybd.znld.service.rbac.dto.RegisterInput;
import com.sybd.znld.service.znld.ILogService;
import com.sybd.znld.util.MyString;
import io.swagger.annotations.*;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;

@Api(tags = "用户接口")
@RestController
@RequestMapping("/api/v1/user")
public class UserController implements IUserController {
    private final DefaultKaptcha defaultKaptcha;
    private final IUserService userService;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final ProjectConfig projectConfig;
    private final BCryptPasswordEncoder encoder;

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Value("${security.oauth2.client.client-id}")
    private String clientId;
    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Autowired
    public UserController(DefaultKaptcha defaultKaptcha,
                          IUserService userService,
                          RedissonClient redissonClient,
                          StringRedisTemplate stringRedisTemplate,
                          ProjectConfig projectConfig) {
        this.defaultKaptcha = defaultKaptcha;
        this.userService = userService;
        this.redissonClient = redissonClient;
        this.stringRedisTemplate = stringRedisTemplate;
        this.projectConfig = projectConfig;
        this.encoder = new BCryptPasswordEncoder(10);
    }

    private String getCaptchaKey(String uuid){
        return "captcha::"+ uuid;
    }

    @ApiOperation(value = "获取验证码图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "会话Id，/[0-9a-f]{32}/", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "login/captcha/{uuid:[0-9a-f]{32}}", produces = {MediaType.IMAGE_PNG_VALUE})
    @Override
    public BufferedImage getCaptcha(@PathVariable("uuid") String uuid, HttpServletRequest request){
        var createText = defaultKaptcha.createText();
        this.stringRedisTemplate.opsForValue().set(getCaptchaKey(uuid), createText, this.projectConfig.getCacheOfCaptchaExpirationTime());
        return defaultKaptcha.createImage(createText);
    }

    @ApiOperation(value = "登入")
    @PostMapping(value = "login", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public LoginResult login(@ApiParam(name = "jsonData", value = "登入数据", required = true) @RequestBody @Valid LoginInput input, HttpServletRequest request, BindingResult bindingResult){
        var key = getCaptchaKey(input.uuid);
        var rightCaptcha = this.stringRedisTemplate.opsForValue().get(key);
        if(rightCaptcha == null) return LoginResult.fail("验证码已失效");
        if(!input.captcha.equalsIgnoreCase(rightCaptcha)) return LoginResult.fail("验证码错误");
        try {
            //input.password = this.encoder.encode(input.password);
            var user = userService.verify(input);
            if(user != null){
                this.stringRedisTemplate.delete(getCaptchaKey(input.uuid));
                long seconds = this.projectConfig.getAuth2TokenExpirationTime().getSeconds();
                return LoginResult.success(user.getId(), clientId, clientSecret, seconds);
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

        String rightCaptcha = request.getSession().getAttribute("captcha").toString();
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
            input.password = this.encoder.encode(input.password);
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
