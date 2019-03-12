package com.sybd.znld.service.mapper;

import com.sybd.znld.model.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int add(UserEntity entity);
    UserEntity verify(String name, String password);
    UserEntity getUserById(String id);
    UserEntity getUserByName(String name);
    int updatePasswordByName(UserEntity entity);
    int updateById(UserEntity entity);
    int updateByName(UserEntity entity);
    Boolean existsByName(String name);
}
