package com.sybd.security.oauth2.server.mapper;

import com.sybd.security.oauth2.server.db.DbSource;
import com.sybd.rbac.model.RoleAuthModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("rbac")
public interface RoleAuthMapper {
    int insert(RoleAuthModel model);
    RoleAuthModel selectById(String id);
    List<RoleAuthModel> selectByRoleId(String id); //一个角色可以关联多个权限
    List<RoleAuthModel> selectByAuthId(String id); //一个权限可以关联多个角色
    RoleAuthModel selectByRoleIdAndAuthId(String roleId, String authId);
}
