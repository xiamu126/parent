package com.sybd.znld.account.controller.user;

import com.sybd.znld.account.controller.user.dto.LoginResult;
import com.sybd.znld.account.controller.user.dto.LogoutResult;
import com.sybd.znld.account.model.LoginInput;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import org.springframework.validation.BindingResult;
import javax.servlet.http.HttpServletRequest;

interface IUserController {
    ApiResult register(RegisterInput input, HttpServletRequest request, BindingResult bindingResult);
    String getCaptcha(HttpServletRequest request);
    ApiResult login(LoginInput input, HttpServletRequest request, BindingResult bindingResult);
    ApiResult logout(String id, HttpServletRequest request);
    ApiResult getUserInfo(String id, HttpServletRequest request);
    ApiResult updateUserInfo(UserModel input, HttpServletRequest request, BindingResult bindingResult);
}
