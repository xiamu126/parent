package com.sybd.znld.service.rbac.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.rbac.model.AuthGroupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface AuthGroupMapper {
    int insert(AuthGroupModel model);
    AuthGroupModel selectById(String id);
    AuthGroupModel selectByName(String name);
}
