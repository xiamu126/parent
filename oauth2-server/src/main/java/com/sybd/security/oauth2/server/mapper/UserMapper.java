package com.sybd.security.oauth2.server.mapper;

import com.sybd.znld.model.rbac.UserModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
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
}
