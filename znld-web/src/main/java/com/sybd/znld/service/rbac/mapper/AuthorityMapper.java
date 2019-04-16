package com.sybd.znld.service.rbac.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.rbac.model.AuthorityModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface AuthorityMapper {
    int insert(AuthorityModel model);
    AuthorityModel selectById(String id);
    AuthorityModel selectByName(String name);
}
