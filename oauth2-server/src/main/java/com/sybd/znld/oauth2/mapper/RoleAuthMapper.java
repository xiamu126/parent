package com.sybd.znld.oauth2.mapper;

import com.sybd.znld.oauth2.db.DbSource;
import com.sybd.znld.model.rbac.RoleAuthorityGroupModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("rbac")
public interface RoleAuthMapper {
    int insert(RoleAuthorityGroupModel model);
    RoleAuthorityGroupModel selectById(String id);
    List<RoleAuthorityGroupModel> selectByRoleId(String id); //一个角色可以关联多个权限
    List<RoleAuthorityGroupModel> selectByAuthId(String id); //一个权限可以关联多个角色
    RoleAuthorityGroupModel selectByRoleIdAndAuthId(String roleId, String authId);
}
