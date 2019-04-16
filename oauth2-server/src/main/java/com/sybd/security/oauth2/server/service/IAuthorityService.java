package com.sybd.security.oauth2.server.service;

import com.sybd.rbac.model.*;
import com.sybd.rbac.model.dto.AuthPackByUser;

import java.util.List;

public interface IAuthorityService {
    AuthGroupModel addAuthGroup(AuthGroupModel model);
    AuthorityModel addAuth(AuthorityModel model);
    RoleModel addRole(RoleModel model);
    UserRoleModel addUserRole(UserRoleModel model);
    RoleAuthModel addRoleAuth(RoleAuthModel model);
    List<AuthorityModel> getAuthoritiesByUserId(String userId);
    List<AuthPackByUser> getAuthPackByUserId(String userId);
    List<RoleModel> getRolesByUserId(String userId);
}
