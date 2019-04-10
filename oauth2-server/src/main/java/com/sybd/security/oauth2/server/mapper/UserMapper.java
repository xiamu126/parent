package com.sybd.security.oauth2.server.mapper;

import com.sybd.security.oauth2.server.db.DbSource;
import com.sybd.znld.model.rbac.RoleModel;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.AuthPackByUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("rbac")
public interface UserMapper {
    int insert(UserModel model);
    UserModel selectById(String id);
    UserModel selectByName(String name);
    UserModel selectByPhone(String phone);
    UserModel selectByEmail(String email);
    UserModel selectByIdCardNo(String idCardNo);
    List<UserModel> selectByOrganizationId(String organizationId);
    int updateById(UserModel user);
    UserModel selectByNameAndPassword(String name, String password);
    int updatePasswordByName(UserModel model);
    int updateByName(UserModel model);
    List<AuthPackByUser> selectAuthPackByUserId(String userId);
    List<RoleModel> selectRolesByUserId(String userId);
}
