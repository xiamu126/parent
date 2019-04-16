package com.sybd.security.oauth2.server.mapper;

import com.sybd.security.oauth2.server.db.DbSource;
import com.sybd.rbac.model.AuthGroupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface AuthGroupMapper {
    int insert(AuthGroupModel model);
    AuthGroupModel selectById(String id);
    AuthGroupModel selectByName(String name);
}
