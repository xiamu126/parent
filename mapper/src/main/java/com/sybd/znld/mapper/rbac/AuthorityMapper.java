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
    AuthorityModel selectByAuthGroupIdAndAuthName(@Param("authGroupId") String authGroupId, @Param("authName") String authName);
    List<AuthorityModel> selectByAuthGroupId(String id);
    List<AuthorityModel> selectByAuthGroupIdAndApp(@Param("authGroupId") String authGroupId, @Param("app") String app);
    List<AuthorityModel> selectByAuthGroupIdAndAppAndType(@Param("authGroupId") String authGroupId, @Param("app") String app, @Param("type") String type);
}
