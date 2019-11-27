package com.sybd.znld.service.rbac;

import com.sybd.znld.model.rbac.AuthorityModel;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.LoginInput;
import com.sybd.znld.model.rbac.dto.InitAccountInput;
import com.sybd.znld.model.rbac.dto.RegisterInput;

import java.util.List;

public interface IUserService {
    UserModel addUser(UserModel model);
    UserModel modifyUserById(UserModel model);
    UserModel modifyUserByName(UserModel model);
    UserModel getUserById(String id);
    UserModel getUserByName(String name);
    List<UserModel> getUserByOrganizationId(String organizationId);
    UserModel verify(String name, String password);
    UserModel verify(LoginInput input);
    UserModel register(RegisterInput input);
    List<AuthorityModel> getAuthoritiesById(String userId);
    void initAccount(InitAccountInput input);
}
