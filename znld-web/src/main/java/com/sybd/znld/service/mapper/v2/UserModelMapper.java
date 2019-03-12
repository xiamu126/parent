package com.sybd.znld.service.mapper.v2;

import com.sybd.znld.model.v2.rbac.UserModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserModelMapper {
    int insert(UserModel model);
    UserModel selectById(String id);
    UserModel selectByName(String name);
    UserModel selectByPhone(String phone);
    UserModel selectByEmail(String email);
    UserModel selectByIdCardNo(String idCardNo);
    List<UserModel> selectByOrganizationId(String organizationId);
    int updateById(UserModel user);
}
