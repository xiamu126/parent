package com.sybd.znld.service.rbac;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.sybd.znld.mapper.rbac.*;
import com.sybd.znld.model.DbDeleteResult;
import com.sybd.znld.model.rbac.*;
import com.sybd.znld.model.rbac.dto.*;
;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RbacService implements IRbacService {
    private final AuthorityGroupMapper authorityGroupMapper;
    private final AuthorityMapper authorityMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;
    private final RoleAuthorityGroupMapper roleAuthorityGroupMapper;
    private final OrganizationMapper organizationMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public RbacService(AuthorityGroupMapper authorityGroupMapper,
                       AuthorityMapper authorityMapper,
                       RoleMapper roleMapper,
                       UserRoleMapper userRoleMapper,
                       UserMapper userMapper,
                       RoleAuthorityGroupMapper roleAuthorityGroupMapper,
                       OrganizationMapper organizationMapper) {
        this.authorityGroupMapper = authorityGroupMapper;
        this.authorityMapper = authorityMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.userMapper = userMapper;
        this.roleAuthorityGroupMapper = roleAuthorityGroupMapper;
        this.organizationMapper = organizationMapper;
    }

    @Override
    public AuthorityGroupModel addAuthGroup(AuthorityGroupModel model) {
        if(model == null) return null;
        if(MyString.isEmptyOrNull(model.name)){
            log.debug("传入的name为空"); return null;
        }
        if(MyNumber.isNegative(model.status)){
            log.debug("传入的status为负数"); return null;
        }
        if(this.authorityGroupMapper.selectByName(model.name) != null){
            log.debug("已经存在名为["+model.name+"]的权限组"); return null;
        }
        // id由数据库生成，不检查合法性
        if(this.authorityGroupMapper.insert(model) > 0) return model;
        return null;
    }

    @Override
    public AuthorityModel addAuth(AuthorityModel model) {
        if(model == null) return null;
        // 判断权限名称，名称可以重复，即不同的组织可以有相同名称的权限
        if(MyString.isEmptyOrNull(model.name)){
            log.debug("传入的name为空"); return null;
        }
        if(this.authorityMapper.insert(model) > 0) return model;
        return null;
    }

    @Override
    public RoleModel addRole(RoleModel model) {
        if(model == null) return null;
        // 判断角色名称，名称可以重复，即不同的组织可以有相同名称的角色
        // 注意，不可以让不同的组织共用相同的角色，如此、会出现相同的角色关联不同组织的权限，
        // 即最终会出现相同的角色关联了多个不同组织的URI
        if(MyString.isEmptyOrNull(model.name)){
            log.debug("名称不能为空"); return null;
        }
        if(!MyString.isUuid(model.organizationId)){
            log.debug("错误的组织id"); return null;
        }
        if(this.organizationMapper.selectById(model.organizationId) == null){
            log.debug("指定的组织id不存在"); return null;
        }
        // 相同组织下不能有相同的角色
        if(this.roleMapper.selectByNameAndOrganId(model.name, model.organizationId) != null){
            log.debug("此组织["+model.organizationId+"]下已经存在相同名称的角色"); return null;
        }
        if(this.roleMapper.insert(model) > 0) return model;
        return null;
    }

    @Override
    public UserRoleModel addUserRole(UserRoleModel model) {
        if(model == null) return null;
        if(!MyString.isUuid(model.userId)){
            log.debug("非法的用户id"); return null;
        }
        if(!MyString.isUuid(model.roleId)){
            log.debug("非法的角色id"); return null;
        }
        if(this.roleMapper.selectById(model.roleId) == null){
            log.debug("指定的角色id不存在"); return null;
        }
        if(this.userMapper.selectById(model.userId) == null){
            log.debug("指定的用户id不存在"); return null;
        }
        if(this.userRoleMapper.selectByUserIdAndRoleId(model.userId, model.roleId) != null){
            log.debug("指定的用户+角色已经存在"); return null;
        }
        if(this.userRoleMapper.insert(model) > 0) return model;
        return null;
    }

    @Override
    public RoleAuthorityGroupModel addRoleAuth(RoleAuthorityGroupModel model) {
        if(model == null) return null;
        if(!MyString.isUuid(model.roleId)){
            log.debug("非法的角色id"); return null;
        }
        if(this.roleMapper.selectById(model.roleId) == null){
            log.debug("指定的角色id不存在"); return null;
        }
        if(this.authorityMapper.selectById(model.authorityGroupId) == null){
            log.debug("指定的权限id不存在"); return null;
        }
        if(this.roleAuthorityGroupMapper.selectByRoleIdAndAuthGroupId(model.roleId, model.authorityGroupId) != null){
            log.debug("指定的角色+权限已经存在"); return null;
        }
        if(this.roleAuthorityGroupMapper.insert(model) > 0) return model;
        return null;
    }

    @Override
    public List<AuthorityModel> getAuthoritiesByUserId(String userId) {
        var roles = this.userRoleMapper.selectByUserId(userId);
        var authList = new ArrayList<AuthorityModel>();
        roles.forEach(r -> {
            var roleAuthList = this.roleAuthorityGroupMapper.selectByRoleId(r.roleId);
            roleAuthList.forEach(ra -> {
                var auth = this.authorityMapper.selectById(ra.authorityGroupId);
                authList.add(auth);
            });
        });
        return authList.size() > 0 ? authList : null;
    }

    @Override
    public OrganizationModel addOrganization(OrganizationModel model) {
        if(model == null || !model.isValidForInsert()) {
            log.debug("输入参数为空");
            return null;
        }
        // 不可以有同名的组织（组织名称唯一，类似于公司名称）
        var organization = this.organizationMapper.selectByName(model.name);
       /* if(organization != null){
            if(organization.status == OrganizationModel.Status.DELETED){
                log.debug("已存在同名的组织，但这个组织处在删除状态，更新为可用状态");
                organization.status = OrganizationModel.Status.OK;
                this.organizationMapper.updateById(organization);
                return organization;
            }
            log.debug("已存在同名的组织，且这个组织处在非删除状态，无法新增");
            return null;
        }*/
        if(this.organizationMapper.insert(model) > 0) return model;
        return null;
    }

    @Override
    public OrganizationModel getOrganizationByName(String name) {
        if(MyString.isEmptyOrNull(name)) return null;
        return this.organizationMapper.selectByName(name);
    }

    @Override
    public OrganizationModel getOrganizationById(String id) {
        if(MyString.isEmptyOrNull(id)) return null;
        return this.organizationMapper.selectById(id);
    }

    @Override
    public OrganizationModel modifyOrganization(OrganizationModel organization) {
        if(organization == null || !MyString.isUuid(organization.id)) return null;
        if(this.organizationMapper.updateById(organization) > 0) return organization;
        return null;
    }

    @Override
    public List<OrganizationModel> getOrganizationByParenIdAndPosition(String parentId, Integer position) {
        if(!parentId.equals("") && !MyString.isUuid(parentId)) return null;
        if(MyNumber.isNegative(position)) return null;
        return null;
    }

    @Override
    public DbDeleteResult removeOrganizationById(String id) {
        if(MyString.isEmptyOrNull(id) || !MyString.isUuid(id)) return DbDeleteResult.PARAM_ERROR;
        //查看此parentId是否被其他人引用过，如果存在有效引用，则不能删除
        /*var ret = this.organizationMapper.selectByParentId(id);
        if(ret != null && !ret.isEmpty()) return DbDeleteResult.CASCADE_ERROR;
        var organization = this.organizationMapper.selectById(id);
        if(organization.status == OrganizationModel.Status.DELETED) return DbDeleteResult.ALREADY_DELETED;
        if(this.organizationMapper.deleteById(id, OrganizationModel.Status.DELETED) > 0) return DbDeleteResult.SUCCESS;
        return DbDeleteResult.NOT_FOUND;*/
        return null;
    }

    @Override
    public DbDeleteResult removeOrganizationByName(String name) {
        if(MyString.isEmptyOrNull(name)) return DbDeleteResult.PARAM_ERROR;
        var organization = this.organizationMapper.selectByName(name);
        if(organization == null) return DbDeleteResult.NOT_FOUND;
        return removeOrganizationById(organization.id);
    }

    @Override
    public List<OrganizationModel> getOrganizationByParenId(String parentId) {
        if(!MyString.isUuid(parentId)) return null;
        //return this.organizationMapper.selectByParentId(parentId);
        return null;
    }

    @Override
    public List<RbacApiInfoSummary> getRbacApiInfoByUserId(String userId) {
        List<RbacApiInfoSummary> results = new ArrayList<>();
        var userRoleModels = this.userRoleMapper.selectByUserId(userId);
        return results;
    }

    @Override
    public List<RbacWebInfoSummary> getRbacWebInfoByUserId(String userId, String app) {
        List<RbacWebInfoSummary> results = new ArrayList<>();
        var userRoleModels = this.userRoleMapper.selectByUserId(userId);
        // 根据角色获取权限，一个账号可以关联多个角色
        return results;
    }

    @Override
    public AuthorityModel addWebAuth(String authGroupId, RbacWebInfo rbacWebInfo, String authName) {
        // 检测权限组是否存在
        var authGroup = this.authorityGroupMapper.selectById(authGroupId);
        if(authGroup == null) {
            log.debug("指定的权限组不存在");
            return null;
        }
        // 获取组织信息
        var organ = this.organizationMapper.selectById(authGroup.organizationId);
        if(organ == null) {
            log.debug("获取组织信息失败");
            return null;
        }
        // 检测此组织名下的所有权限是否已经包含了相同的权限
        // 同一组织下的同一个权限组不能包含相同的权限（包括name和uri）
        var authority = this.authorityMapper.selectByOrganIdAndAuthName(authGroupId, authName);
        if(authority != null){
            log.debug("这个权限组下已经存在名为["+authName+"]的权限");
            // 如果已经存在，就append
            return null;
        }
        return null;
    }

    @Override
    public AuthorityModel addApiAuth(String authGroupId, RbacApiInfo rbacApiInfo, String authName) {
        // 检测权限组是否存在
        var authGroup = this.authorityGroupMapper.selectById(authGroupId);
        if(authGroup == null) {
            log.debug("指定的权限组不存在");
            return null;
        }
        // 获取组织信息
        var organ = this.organizationMapper.selectById(authGroup.organizationId);
        if(organ == null) {
            log.debug("获取组织信息失败");
            return null;
        }
        // 检测此组织名下的所有权限是否已经包含了相同的权限
        // 同一组织下的同一个权限组不能包含相同的权限（包括name和uri）
        var authValue = rbacApiInfo.toJson();
        return null;
    }

    @Override
    public RoleAuthorityGroupModel addAuthToRole(String authId, String roleId) {
        if(!MyString.isUuid(authId) || !MyString.isUuid(roleId)) {
            log.debug("非法的参数");
            return null;
        }
        if(this.authorityMapper.selectById(authId) == null){
            log.debug("指定的权限不存在");
            return null;
        }
        if(this.roleMapper.selectById(roleId) == null){
            log.debug("指定的角色不存在");
            return null;
        }
        // 判断是否已经存在
        if(this.roleAuthorityGroupMapper.selectByRoleIdAndAuthGroupId(roleId, authId) != null){
            log.debug("此权限与此角色已经关联，无法再次绑定");
            return null;
        }
        var model = new RoleAuthorityGroupModel();
        model.authorityGroupId = authId;
        model.roleId = roleId;
        if(this.roleAuthorityGroupMapper.insert(model) > 0) return model;
        return null;
    }
}
