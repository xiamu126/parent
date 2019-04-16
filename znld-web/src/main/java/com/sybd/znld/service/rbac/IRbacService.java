package com.sybd.znld.service.rbac;

import com.sybd.rbac.model.*;
import com.sybd.db.model.DbDeleteResult;
import com.sybd.rbac.model.dto.RbacApiInfo;
import com.sybd.rbac.model.dto.RbacHtmlInfo;
import com.sybd.rbac.model.dto.RbacInfo;

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
    AuthorityModel addHtmlAuth(String authGroupId, RbacHtmlInfo rbacHtmlInfo, String authName);
    AuthorityModel addApiAuth(String authGroupId, RbacApiInfo rbacApiInfo, String authName);
    RoleAuthModel addAuthToRole(String authId, String roleId);
}
