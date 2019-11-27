package com.sybd.znld.mapper.rbac;

import com.sybd.znld.model.rbac.RoleModel;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.AuthPackByUser;

import java.util.List;

public interface BaseUserMapper {
    int insert(UserModel model);
    UserModel selectById(String id);
    UserModel selectByName(String name);
    List<UserModel> selectByOrganizationId(String organizationId);
    int updateById(UserModel user);
    UserModel selectByNameAndPassword(String name, String password);
    int updatePasswordByName(UserModel model);
    int updateByName(UserModel model);
    List<AuthPackByUser> selectAuthPackByUserId(String userId);
    List<RoleModel> selectRolesByUserId(String userId);
}
