package com.sybd.znld.service.rbac;

import com.sybd.znld.model.db.DbDeleteResult;
import com.sybd.znld.model.rbac.*;
import com.sybd.znld.model.rbac.dto.*;

import java.util.List;

public interface IRbacService {
    AuthorityGroupModel addAuthGroup(AuthorityGroupModel model);
    AuthorityModel addAuth(AuthorityModel model);
    RoleModel addRole(RoleModel model);
    UserRoleModel addUserRole(UserRoleModel model);
    RoleAuthorityGroupModel addRoleAuth(RoleAuthorityGroupModel model);
    List<AuthorityModel> getAuthoritiesByUserId(String userId);
    OrganizationModel addOrganization(OrganizationModel organization);
    OrganizationModel getOrganizationByName(String name);
    OrganizationModel getOrganizationById(String id);
    OrganizationModel modifyOrganization(OrganizationModel organization);
    List<OrganizationModel> getOrganizationByParenIdAndPosition(String parentId, Integer position);
    DbDeleteResult removeOrganizationById(String id);
    DbDeleteResult removeOrganizationByName(String name);
    List<OrganizationModel> getOrganizationByParenId(String parentId);
    List<RbacApiInfoSummary> getRbacApiInfoByUserId(String userId);
    List<RbacWebInfoSummary> getRbacWebInfoByUserId(String userId, String app);
    AuthorityModel addWebAuth(String authGroupId, RbacWebInfo rbacHtmlInfo, String authName);
    AuthorityModel addApiAuth(String authGroupId, RbacApiInfo rbacApiInfo, String authName);
    RoleAuthorityGroupModel addAuthToRole(String authId, String roleId);
}
