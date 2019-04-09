package com.sybd.znld.service.rbac.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.rbac.OrganizationModel;
import com.sybd.znld.model.rbac.dto.AuthPackByGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("rbac")
public interface OrganizationMapper {
    int insert(OrganizationModel model);
    OrganizationModel selectByName(String name);
    OrganizationModel selectById(String id);
    int updateById(OrganizationModel model);
    List<OrganizationModel> selectByParentIdAndPosition(String parentId, Integer position);
    int deleteById(String id, Short status);
    List<OrganizationModel> selectByParentId(String parentId);
    List<AuthPackByGroup> selectAuthPackByGroupId(String authGroupId);
    AuthPackByGroup selectAuthPackByGroupIdAuthValueAndName(String authGroupId, String authValue, String authName);
}