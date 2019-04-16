package com.sybd.znld.service.rbac.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.rbac.model.UserRoleModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("rbac")
public interface UserRoleMapper {
    int insert(UserRoleModel model);
    UserRoleModel selectById(String id);
    List<UserRoleModel> selectByUserId(String name); //一个用户可以关联多个角色
    List<UserRoleModel> selectByRoleId(String name); //一个角色可以关联多个用户
    UserRoleModel selectByUserIdAndRoleId(String userId, String roleId);
}
