package com.sybd.znld.service.rbac.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.rbac.OrganizationAuthGroupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("rbac")
public interface OrganizationAuthGroupMapper {
    OrganizationAuthGroupModel selectByAuthGroupId(String authGroupId);
}
