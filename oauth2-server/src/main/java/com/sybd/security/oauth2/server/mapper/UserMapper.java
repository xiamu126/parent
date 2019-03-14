package com.sybd.security.oauth2.server.mapper;

import com.sybd.security.oauth2.server.model.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserEntity selectByName(String name);
}
