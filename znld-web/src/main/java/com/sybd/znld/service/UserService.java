package com.sybd.znld.service;

import com.sybd.znld.service.model.user.UserEntity;
import com.sybd.znld.service.model.user.dto.LoginInput;
import com.sybd.znld.service.model.user.dto.RegisterInput;

public interface UserService extends BaseService {
    UserEntity add(RegisterInput input);
    UserEntity verify(LoginInput input);
    UserEntity verify(String name, String password);
    UserEntity getUserById(String id);
    UserEntity getUserByName(String name);
    UserEntity updateById(UserEntity input);
    UserEntity updateByName(UserEntity input);
    UserEntity register(RegisterInput input);
}
