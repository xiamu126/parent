package com.sybd.security.oauth2.server.mapper;

import com.sybd.security.oauth2.server.db.DbSource;
import com.sybd.znld.model.rbac.OrganizationAuthGroupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface OrganizationAuthGroupMapper {
    OrganizationAuthGroupModel selectByAuthGroupId(String authGroupId);
}
