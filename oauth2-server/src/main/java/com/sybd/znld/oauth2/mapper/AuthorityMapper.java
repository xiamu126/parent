package com.sybd.znld.oauth2.mapper;

import com.sybd.znld.oauth2.db.DbSource;
import com.sybd.znld.model.rbac.AuthorityModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface AuthorityMapper {
    int insert(AuthorityModel model);
    AuthorityModel selectById(String id);
    AuthorityModel selectByName(String name);
}
