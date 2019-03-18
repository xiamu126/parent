package com.sybd.znld.service.rbac;

import com.sybd.znld.model.rbac.UserModel;

import java.util.List;

public interface IUserService {
    UserModel addUser(UserModel user);
    UserModel modifyUserById(UserModel user);
    UserModel getUserById(String id);
    UserModel getUserByName(String name);
    UserModel getUserByPhone(String phone);
    UserModel getUserByEmail(String email);
    UserModel getUserByIdCardNo(String idCardNo);
    List<UserModel> getUserByOrganizationId(String organizationId);
}
