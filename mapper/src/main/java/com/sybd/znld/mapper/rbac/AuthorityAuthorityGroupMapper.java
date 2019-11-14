package com.sybd.znld.mapper.rbac;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.rbac.AuthorityAuthorityGroupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface AuthorityAuthorityGroupMapper {
    int insert(AuthorityAuthorityGroupModel model);
    AuthorityAuthorityGroupModel selectById(String id);
    int deleteById(String id);
}
