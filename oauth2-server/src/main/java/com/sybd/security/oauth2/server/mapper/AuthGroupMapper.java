package com.sybd.security.oauth2.server.mapper;

import com.sybd.znld.model.rbac.AuthGroupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthGroupMapper {
    int insert(AuthGroupModel model);
    AuthGroupModel selectById(String id);
    AuthGroupModel selectByName(String name);
}
