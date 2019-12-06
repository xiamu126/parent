package com.sybd.znld.mapper.rbac;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.rbac.OrganizationModel;
import com.sybd.znld.model.rbac.dto.AuthPackByGroup;
import com.sybd.znld.model.rbac.dto.CityAndCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DbSource("rbac")
public interface OrganizationMapper {
    int insert(OrganizationModel model);
    OrganizationModel selectByName(String name);
    OrganizationModel selectById(String id);
    List<OrganizationModel> selectAll();
    int updateById(OrganizationModel model);
    int deleteById(String id);
    List<CityAndCode> selectAllCityAndCode();
}