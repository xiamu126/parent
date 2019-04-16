package com.sybd.security.oauth2.server.mapper;

import com.sybd.security.oauth2.server.db.DbSource;
import com.sybd.rbac.model.RoleModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface RoleMapper {
    int insert(RoleModel model);
    RoleModel selectById(String id);
    RoleModel selectByName(String name);
    RoleModel selectByNameAndOrganId(String name, String organizationId);
}
