package com.sybd.znld.web.controller.user;

import com.sybd.znld.model.rbac.dto.LoginInput;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import com.sybd.znld.web.controller.user.dto.LoginResult;
import com.sybd.znld.web.controller.user.dto.LogoutResult;
import com.sybd.znld.znld.core.ApiResult;
import com.sybd.znld.model.rbac.UserModel;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;

interface IUserController {
    ApiResult register(RegisterInput input, HttpServletRequest request, BindingResult bindingResult);
    BufferedImage getCaptcha(String uuid, HttpServletRequest request);
    LoginResult login(LoginInput input, HttpServletRequest request, BindingResult bindingResult);
    ApiResult verifyCaptcha(String captcha, HttpServletRequest request);
    LogoutResult logout(String id, HttpServletRequest request);
    ApiResult getUserInfo(String id, HttpServletRequest request);
    ApiResult updateUserInfo(UserModel input, HttpServletRequest request, BindingResult bindingResult);
}
