package com.sybd.znld.service.rbac.mapper;

import com.sybd.znld.model.rbac.RoleModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper {
    int insert(RoleModel model);
    RoleModel selectById(String id);
    RoleModel selectByName(String name);
}
