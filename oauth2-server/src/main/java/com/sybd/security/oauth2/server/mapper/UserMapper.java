package com.sybd.security.oauth2.server.mapper;

import com.sybd.znld.model.rbac.UserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserModel selectByName(String name);
}
