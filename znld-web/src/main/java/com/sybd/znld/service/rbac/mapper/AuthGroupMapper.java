package com.sybd.znld.service.rbac.mapper;

import com.sybd.znld.model.rbac.AuthGroupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthGroupMapper {
    int insert(AuthGroupModel model);
    AuthGroupModel selectById(String id);
    AuthGroupModel selectByName(String name);
}
