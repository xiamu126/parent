package com.sybd.znld.account.controller.user;

import com.sybd.znld.account.controller.user.dto.LoginResult;
import com.sybd.znld.account.model.LoginInput;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "用户接口")
@RequestMapping("/api/v2/user")
interface IUserController {
    @ApiOperation(value = "登入")
    @PostMapping(value = "login", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResult login(@ApiParam(name = "jsonData", value = "登入数据", required = true) @RequestBody @Valid LoginInput input,
                           HttpServletRequest request, BindingResult bindingResult);
    @PostMapping(value = "register", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResult register(@RequestBody @Valid RegisterInput input, HttpServletRequest request, BindingResult bindingResult);
    @ApiOperation(value = "获取验证码")
    @GetMapping(value = "login/captcha", produces = {MediaType.APPLICATION_JSON_VALUE})
    String getCaptcha(HttpServletRequest request);
    // 获取用户的登入信息，如果存在的话
    @GetMapping(value = "login/{id:[0-9a-f]{32}}", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResult getUserLoginInfo(@PathVariable(name = "id") String id);
    // 获取用户的注册信息
    @GetMapping(value = "register/{id:[0-9a-f]{32}}", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResult getUserInfo(@PathVariable(name = "id") String id, HttpServletRequest request);

    ApiResult logout(String id, HttpServletRequest request);
    ApiResult updateUserInfo(UserModel input, HttpServletRequest request, BindingResult bindingResult);
}
