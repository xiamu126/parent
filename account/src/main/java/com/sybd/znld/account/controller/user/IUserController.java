package com.sybd.znld.account.controller.user;

import com.sybd.znld.account.controller.user.dto.LoginResult;
import com.sybd.znld.account.controller.user.dto.LogoutResult;
import com.sybd.znld.account.model.LoginInput;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

interface IUserController {
    @ApiOperation(value = "登入")
    @PostMapping(value = "login", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResult login(@ApiParam(name = "jsonData", value = "登入数据", required = true) @RequestBody @Valid LoginInput input,
                           HttpServletRequest request, BindingResult bindingResult);
    ApiResult register(RegisterInput input, HttpServletRequest request, BindingResult bindingResult);
    String getCaptcha(HttpServletRequest request);
    ApiResult logout(String id, HttpServletRequest request);
    ApiResult getUserInfo(String id, HttpServletRequest request);
    ApiResult updateUserInfo(UserModel input, HttpServletRequest request, BindingResult bindingResult);
}
