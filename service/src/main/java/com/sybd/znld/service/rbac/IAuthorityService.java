package com.sybd.znld.service.rbac;

import com.sybd.znld.model.rbac.*;
import com.sybd.znld.model.rbac.dto.AuthPackByUser;

import java.util.List;

public interface IAuthorityService {
    AuthorityGroupModel addAuthGroup(AuthorityGroupModel model);
    AuthorityModel addAuth(AuthorityModel model);
    RoleModel addRole(RoleModel model);
    UserRoleModel addUserRole(UserRoleModel model);
    RoleAuthorityGroupModel addRoleAuth(RoleAuthorityGroupModel model);
    List<AuthorityModel> getAuthoritiesByUserId(String userId);
    List<AuthPackByUser> getAuthPackByUserId(String userId);
    List<RoleModel> getRolesByUserId(String userId);
}
