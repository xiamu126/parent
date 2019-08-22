package com.sybd.znld.mapper.rbac;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.rbac.RoleModel;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.AuthPackByUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("rbac")
public interface UserMapper extends BaseUserMapper{
}
