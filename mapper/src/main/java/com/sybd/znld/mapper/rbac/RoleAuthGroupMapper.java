package com.sybd.znld.mapper.rbac;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.rbac.RoleAuthorityGroupModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DbSource("rbac")
public interface RoleAuthGroupMapper {
    int insert(RoleAuthorityGroupModel model);
    RoleAuthorityGroupModel selectById(String id);
    List<RoleAuthorityGroupModel> selectByRoleId(String id); //一个角色可以关联多个权限组
    List<RoleAuthorityGroupModel> selectByAuthGroupId(String id); //一个权限可以关联多个角色
    RoleAuthorityGroupModel selectByRoleIdAndAuthGroupId(@Param("roleId") String roleId, @Param("authGroupId") String authGroupId);
}
