package com.sybd.znld.service.rbac;

import com.sybd.znld.model.DbDeleteResult;
import com.sybd.znld.model.rbac.*;
import com.sybd.znld.model.rbac.dto.RbacApiInfo;
import com.sybd.znld.model.rbac.dto.RbacHtmlInfo;
import com.sybd.znld.model.rbac.dto.RbacInfo;
import com.sybd.znld.service.rbac.mapper.*;
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
    private final AuthGroupMapper authGroupMapper;
    private final AuthorityMapper authorityMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;
    private final RoleAuthMapper roleAuthMapper;
    private final OrganizationMapper organizationMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public RbacService(AuthGroupMapper authGroupMapper,
                       AuthorityMapper authorityMapper,
                       RoleMapper roleMapper,
                       UserRoleMapper userRoleMapper,
                       UserMapper userMapper,
                       RoleAuthMapper roleAuthMapper,
                       OrganizationMapper organizationMapper) {
        this.authGroupMapper = authGroupMapper;
        this.authorityMapper = authorityMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.userMapper = userMapper;
        this.roleAuthMapper = roleAuthMapper;
        this.organizationMapper = organizationMapper;
    }

    @Override
    public AuthGroupModel addAuthGroup(AuthGroupModel model) {
        if(model == null) return null;
        if(MyString.isEmptyOrNull(model.name)){
            log.debug("传入的name为空"); return null;
        }
        if(MyNumber.isNegative(model.position)){
            log.debug("传入的position为负数"); return null;
        }
        if(MyNumber.isNegative(model.status)){
            log.debug("传入的status为负数"); return null;
        }
        if(!MyString.isEmptyOrNull(model.parentId)){
            if(!MyString.isUuid(model.parentId)) {
                log.debug("传入的parentId非法"); return null;
            }
            if(this.authGroupMapper.selectById(model.parentId) == null) {
                log.debug("传入的parentId不存在"); return null;
            }
        }
        if(this.authGroupMapper.selectByName(model.name) != null){
            log.debug("已经存在名为["+model.name+"]的权限组"); return null;
        }
        // id由数据库生成，不检查合法性
        if(this.authGroupMapper.insert(model) > 0) return model;
        return null;
    }

    @Override
    public AuthorityModel addAuth(AuthorityModel model) {
        if(model == null) return null;
        // 判断权限名称，名称可以重复，即不同的组织可以有相同名称的权限
        if(MyString.isEmptyOrNull(model.name)){
            log.debug("传入的name为空"); return null;
        }
        // 判断路径
        if(MyString.isEmptyOrNull(model.uri)){
            log.debug("url不能为空"); return null;
        }
        // 判断状态
        if(!AuthorityModel.Status.isValid(model.status)){
            log.debug("非法的status"); return null;
        }
        // 判断权限组
        if(!MyString.isUuid(model.authorityGroupId)){
            log.debug("传入的权限组id非法"); return null;
        }
        if(this.authGroupMapper.selectById(model.authorityGroupId) == null){
            log.debug("指定的权限组不存在"); return null;
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
        if(!RoleModel.Status.isValid(model.status)){
            log.debug("错误的状态"); return null;
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
    public RoleAuthModel addRoleAuth(RoleAuthModel model) {
        if(model == null) return null;
        if(!MyString.isUuid(model.authId)){
            log.debug("非法的权限id"); return null;
        }
        if(!MyString.isUuid(model.roleId)){
            log.debug("非法的角色id"); return null;
        }
        if(this.roleMapper.selectById(model.roleId) == null){
            log.debug("指定的角色id不存在"); return null;
        }
        if(this.authorityMapper.selectById(model.authId) == null){
            log.debug("指定的权限id不存在"); return null;
        }
        if(this.roleAuthMapper.selectByRoleIdAndAuthId(model.roleId, model.authId) != null){
            log.debug("指定的角色+权限已经存在"); return null;
        }
        if(this.roleAuthMapper.insert(model) > 0) return model;
        return null;
    }

    @Override
    public List<AuthorityModel> getAuthoritiesByUserId(String userId) {
        var roles = this.userRoleMapper.selectByUserId(userId);
        var authList = new ArrayList<AuthorityModel>();
        roles.forEach(r -> {
            var roleAuthList = this.roleAuthMapper.selectByRoleId(r.roleId);
            roleAuthList.forEach(ra -> {
                var auth = this.authorityMapper.selectById(ra.authId);
                authList.add(auth);
            });
        });
        return authList.size() > 0 ? authList : null;
    }

    @Override
    public OrganizationModel addOrganization(OrganizationModel model) {
        if(model == null || !model.isValid()) {
            log.debug("输入参数为空");
            return null;
        }
        // 不可以有同名的组织（组织名称唯一，类似于公司名称）
        var organization = this.organizationMapper.selectByName(model.name);
        if(organization != null){
            if(organization.status == OrganizationModel.Status.DELETED){
                log.debug("已存在同名的组织，但这个组织处在删除状态，更新为可用状态");
                organization.status = OrganizationModel.Status.OK;
                this.organizationMapper.updateById(organization);
                return organization;
            }
            log.debug("已存在同名的组织，且这个组织处在非删除状态，无法新增");
            return null;
        }
        // 如果设置了父节点，则检测此父节点是否存在，若没设置意味着顶级节点
        if(!MyString.isEmptyOrNull(model.parentId) && this.organizationMapper.selectById(model.parentId) == null) {
            log.debug("设置了父节点（不为空），但此父节点是无效的");
            return null;
        }
        // 监测position节点，同一个parentId下，不能重复；
        // 一个组织只能由一个顶级节点；也就是组织A''0与组织B''0可以共存
        if(model.parentId.equals("") && model.position == 0){// 是顶级节点

        }
        var ret = this.organizationMapper.selectByParentIdAndPosition(model.parentId, model.position);
        if(ret != null && !ret.isEmpty()) {
            log.debug("已经存在相同的父节点与位置");
            return null;
        }
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
        return this.organizationMapper.selectByParentIdAndPosition(parentId, position);
    }

    @Override
    public DbDeleteResult removeOrganizationById(String id) {
        if(MyString.isEmptyOrNull(id) || !MyString.isUuid(id)) return DbDeleteResult.PARAM_ERROR;
        //查看此parentId是否被其他人引用过，如果存在有效引用，则不能删除
        var ret = this.organizationMapper.selectByParentId(id);
        if(ret != null && !ret.isEmpty()) return DbDeleteResult.CASCADE_ERROR;
        var organization = this.organizationMapper.selectById(id);
        if(organization.status == OrganizationModel.Status.DELETED) return DbDeleteResult.ALREADY_DELETED;
        if(this.organizationMapper.deleteById(id, OrganizationModel.Status.DELETED) > 0) return DbDeleteResult.SUCCESS;
        return DbDeleteResult.NOT_FOUND;
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
        return this.organizationMapper.selectByParentId(parentId);
    }

    @Override
    public RbacInfo getRbacInfo(String userId) {
        var user = this.userMapper.selectById(userId);
        var userRoles = this.userRoleMapper.selectByUserId(userId);
        return null;
    }

    @Override
    public AuthorityModel addHtmlAuth(String authGroupId, RbacHtmlInfo rbacHtmlInfo, String authName) {
        if(!rbacHtmlInfo.isValid()) return null;
        // 检测权限组是否存在
        var authGroup = this.authGroupMapper.selectById(authGroupId);
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
        var authValue = rbacHtmlInfo.getJsonString();
        var pack = this.organizationMapper.selectAuthPackByGroupIdAuthValueAndName(authGroupId, authValue, authName);
        if(pack != null) {
            log.debug("已经存在权限[" + authName + "]: " + authValue);
            return null;
        }
        var authModel = new AuthorityModel();
        authModel.name = authName;
        authModel.uri = authValue;
        authModel.authorityGroupId = authGroupId;
        if(this.authorityMapper.insert(authModel) > 0) return authModel;
        return null;
    }

    @Override
    public AuthorityModel addApiAuth(String authGroupId, RbacApiInfo rbacApiInfo, String authName) {
        if(!rbacApiInfo.isValid()) return null;
        // 检测权限组是否存在
        var authGroup = this.authGroupMapper.selectById(authGroupId);
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
        var authValue = rbacApiInfo.getJsonString();
        var pack = this.organizationMapper.selectAuthPackByGroupIdAuthValueAndName(authGroupId, authValue, authName);
        if(pack != null) {
            log.debug("已经存在权限[" + authName + "]: " + authValue);
            return null;
        }
        var authModel = new AuthorityModel();
        authModel.name = authName;
        authModel.uri = authValue;
        authModel.authorityGroupId = authGroupId;
        if(this.authorityMapper.insert(authModel) > 0) return authModel;
        return null;
    }

    @Override
    public RoleAuthModel addAuthToRole(String authId, String roleId) {
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
        if(this.roleAuthMapper.selectByRoleIdAndAuthId(roleId, authId) != null){
            log.debug("此权限与此角色已经关联，无法再次绑定");
            return null;
        }
        var model = new RoleAuthModel();
        model.authId = authId;
        model.roleId = roleId;
        if(this.roleAuthMapper.insert(model) > 0) return model;
        return null;
    }
}
