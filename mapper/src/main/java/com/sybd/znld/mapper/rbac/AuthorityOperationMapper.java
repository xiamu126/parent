package com.sybd.znld.mapper.rbac;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.rbac.AuthorityOperationModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface AuthorityOperationMapper {
    int insert(AuthorityOperationModel model);
    AuthorityOperationModel selectById(String id);
}
