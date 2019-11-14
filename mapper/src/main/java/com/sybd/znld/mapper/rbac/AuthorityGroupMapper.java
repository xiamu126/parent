package com.sybd.znld.mapper.rbac;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.rbac.AuthorityGroupModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@DbSource("rbac")
public interface AuthorityGroupMapper {
    int insert(AuthorityGroupModel model);
    AuthorityGroupModel selectById(String id);
    AuthorityGroupModel selectByName(String name);
    AuthorityGroupModel selectByOrganIdAndAuthGroupName(@Param("organId") String organId, @Param("authGroupName") String authGroupName);
}
