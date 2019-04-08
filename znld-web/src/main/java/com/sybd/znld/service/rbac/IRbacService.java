package com.sybd.znld.service.rbac;

import com.sybd.znld.model.DbDeleteResult;
import com.sybd.znld.model.rbac.*;
import com.sybd.znld.service.rbac.dto.RbacInfo;

import java.util.List;

public interface IRbacService {
    AuthGroupModel addAuthGroup(AuthGroupModel model);
    AuthorityModel addAuth(AuthorityModel model);
    RoleModel addRole(RoleModel model);
    UserRoleModel addUserRole(UserRoleModel model);
    RoleAuthModel addRoleAuth(RoleAuthModel model);
    List<AuthorityModel> getAuthoritiesByUserId(String userId);
    OrganizationModel addOrganization(OrganizationModel organization);
    OrganizationModel getOrganizationByName(String name);
    OrganizationModel getOrganizationById(String id);
    OrganizationModel modifyOrganization(OrganizationModel organization);
    List<OrganizationModel> getOrganizationByParenIdAndPosition(String parentId, Integer position);
    DbDeleteResult removeOrganizationById(String id);
    DbDeleteResult removeOrganizationByName(String name);
    List<OrganizationModel> getOrganizationByParenId(String parentId);
    RbacInfo getRbacInfo(String userId);
    boolean addAuth(RbacInfo rbacInfo);
}
