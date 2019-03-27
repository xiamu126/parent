package com.sybd.security.oauth2.server.mapper;

import com.sybd.znld.model.rbac.AuthorityModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthorityMapper {
    int insert(AuthorityModel model);
    AuthorityModel selectById(String id);
    AuthorityModel selectByName(String name);
}
