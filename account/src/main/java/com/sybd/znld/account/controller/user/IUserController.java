package com.sybd.znld.account.controller.user;

import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.LoginInput;
import com.sybd.znld.model.rbac.dto.LogoutInput;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "用户接口")
interface IUserController {
    @PostMapping(value = "/api/v2/user/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResult loginV2(@RequestBody LoginInput input, HttpServletRequest request, BindingResult bindingResult);

    @PostMapping(value = "/api/v3/user/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResult loginV3(@RequestBody LoginInput input, HttpServletRequest request, BindingResult bindingResult);

    @PostMapping(value = "/api/v2/user/register", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResult register(@RequestBody @Valid RegisterInput input, HttpServletRequest request, BindingResult bindingResult);

    @GetMapping(value = "/api/v2/user/login/captcha", produces = {MediaType.APPLICATION_JSON_VALUE})
    String getCaptcha(HttpServletRequest request);

    @PostMapping(value = "/api/v2/user/logout", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResult logoutV2(@RequestBody String id, HttpServletRequest request);

    @PostMapping(value = "/api/v3/user/logout", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResult logoutV3(@RequestBody LogoutInput input, HttpServletRequest request);
}
