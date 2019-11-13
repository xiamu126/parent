package com.sybd.znld.mapper.rbac;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.rbac.AuthorityGroupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface AuthGroupMapper {
    int insert(AuthorityGroupModel model);
    AuthorityGroupModel selectById(String id);
    AuthorityGroupModel selectByName(String name);
}
