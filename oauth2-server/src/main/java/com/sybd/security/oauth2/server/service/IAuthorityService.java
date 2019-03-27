package com.sybd.security.oauth2.server.service;

import com.sybd.znld.model.rbac.*;

import java.util.List;

public interface IAuthorityService {
    AuthGroupModel addAuthGroup(AuthGroupModel model);
    AuthorityModel addAuth(AuthorityModel model);
    RoleModel addRole(RoleModel model);
    UserRoleModel addUserRole(UserRoleModel model);
    RoleAuthModel addRoleAuth(RoleAuthModel model);
    List<AuthorityModel> getAuthoritiesByUserId(String userId);
}
