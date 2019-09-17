package com.sybd.znld.oauth2.mapper;

import com.sybd.znld.oauth2.db.DbSource;
import com.sybd.znld.model.rbac.AuthGroupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface AuthGroupMapper {
    int insert(AuthGroupModel model);
    AuthGroupModel selectById(String id);
    AuthGroupModel selectByName(String name);
}
