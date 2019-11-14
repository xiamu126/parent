package com.sybd.znld.mapper.rbac;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.rbac.AuthorityModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DbSource("rbac")
public interface AuthorityMapper {
    int insert(AuthorityModel model);
    int update(AuthorityModel model);
    AuthorityModel selectById(String id);
    AuthorityModel selectByOrganIdAndAuthName(@Param("organId") String organId, @Param("authName") String authName);
}
