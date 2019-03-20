package com.sybd.znld.controller.user;

import com.sybd.znld.controller.user.dto.LoginResult;
import com.sybd.znld.controller.user.dto.LogoutResult;
import com.sybd.znld.core.ApiResult;
import com.sybd.znld.service.model.user.UserEntity;
import com.sybd.znld.service.model.user.dto.LoginInput;
import com.sybd.znld.service.model.user.dto.RegisterInput;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

interface IUserController {
    ApiResult register(RegisterInput input, BindingResult bindingResult);
    BufferedImage getCaptcha(String uuid, HttpServletRequest request, HttpServletResponse response);
    LoginResult login(LoginInput input, HttpServletRequest request, BindingResult bindingResult);
    ApiResult verifyCaptcha(String captcha, HttpServletRequest request);
    LogoutResult logout(String id, HttpServletRequest request);
    ApiResult getUserInfo(String id);
    ApiResult updateUserInfo(UserEntity input, BindingResult bindingResult);
}
