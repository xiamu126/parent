package com.sybd.znld.account.service;

import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.rbac.AuthorityModel;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IUserService {
    ApiResult loginV3(LoginInput input, HttpServletRequest request);
    ApiResult logoutV3(String token);
    UserModel addUser(UserModel model);
    UserModel register(RegisterInput input);
}
