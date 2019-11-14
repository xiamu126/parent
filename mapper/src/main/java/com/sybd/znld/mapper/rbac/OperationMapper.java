package com.sybd.znld.mapper.rbac;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.rbac.OperationModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@DbSource("rbac")
public interface OperationMapper {
    int insert(OperationModel model);
    OperationModel selectById(String id);
    OperationModel selectByOrganIdAnaName(@Param("organId") String organId, @Param("name") String name);
}
