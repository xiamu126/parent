package com.sybd.znld.service.mapper.v2;

import com.sybd.znld.model.v2.rbac.UserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserModelMapper {
    int insert(UserModel model);
}
