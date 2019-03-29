package com.sybd.znld.service.rbac;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.rbac.*;
import com.sybd.znld.service.rbac.mapper.*;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorityService implements IAuthorityService {
    private final Logger log = LoggerFactory.getLogger(AuthorityService.class);
    private final AuthGroupMapper authGroupMapper;
    private final AuthorityMapper authorityMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;
    private final RoleAuthMapper roleAuthMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public AuthorityService(AuthGroupMapper authGroupMapper, AuthorityMapper authorityMapper,
                            RoleMapper roleMapper, UserRoleMapper userRoleMapper, UserMapper userMapper, RoleAuthMapper roleAuthMapper) {
        this.authGroupMapper = authGroupMapper;
        this.authorityMapper = authorityMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.userMapper = userMapper;
        this.roleAuthMapper = roleAuthMapper;
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
        if(MyString.isEmptyOrNull(model.name)){
            log.debug("传入的name为空"); return null;
        }
        if(!MyString.isUuid(model.authorityGroupId)){
            log.debug("传入的权限组id非法"); return null;
        }
        if(MyString.isEmptyOrNull(model.url)){
            log.debug("url不能为空"); return null;
        }
        if(!AuthorityModel.Type.isValid(model.type)){
            log.debug("非法的type"); return null;
        }
        if(!AuthorityModel.Status.isValid(model.status)){
            log.debug("非法的status"); return null;
        }
        if(this.authorityMapper.selectByName(model.name) != null){
            log.debug("名称为["+model.name+"]已经存在"); return null;
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
        if(MyString.isEmptyOrNull(model.name)){
            log.debug("名称不能为空"); return null;
        }
        if(!RoleModel.Type.isValid(model.type)){
            log.debug("错误的角色类型"); return null;
        }
        if(!RoleModel.Status.isValid(model.status)){
            log.debug("错误的状态"); return null;
        }
        if(this.roleMapper.selectByName(model.name) != null){
            log.debug("已经存在名为["+model.name+"]的角色"); return null;
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
}
