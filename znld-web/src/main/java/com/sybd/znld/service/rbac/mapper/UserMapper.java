package com.sybd.znld.service.rbac.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.service.rbac.dto.AuthPack;
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
    List<AuthPack> selectAuthPackByUserId(String userId);
}
