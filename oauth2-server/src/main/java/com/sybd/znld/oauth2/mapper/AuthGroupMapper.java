package com.sybd.znld.oauth2.mapper;

import com.sybd.znld.oauth2.db.DbSource;
import com.sybd.znld.model.rbac.AuthorityGroupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface AuthGroupMapper {
    int insert(AuthorityGroupModel model);
    AuthorityGroupModel selectById(String id);
    AuthorityGroupModel selectByName(String name);
}
