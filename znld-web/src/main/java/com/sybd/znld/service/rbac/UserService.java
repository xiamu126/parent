package com.sybd.znld.service.rbac;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.service.model.rbac.UserModel;
import com.sybd.znld.service.mapper.rbac.UserModelMapper;
import com.whatever.util.MyString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@DbSource("rbac")
public class UserService implements IUserService {
    private final UserModelMapper userModelMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public UserService(UserModelMapper userModelMapper) {
        this.userModelMapper = userModelMapper;
    }

    @Override
    //@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public UserModel addUser(UserModel user) {
        if(user == null) return null;
        if(this.userModelMapper.selectByName(user.name) != null) return null;
        if(this.userModelMapper.insert(user) > 0) return user; else return null;
    }

    @Override
    public UserModel modifyUserById(UserModel user) {
        if(!MyString.isUuid(user.id)) return null;
        //如果存在有效的电话号码，则需要验证此号码是否已经使用过
        if(user.phone != null && !user.phone.isEmpty() && MyString.isPhoneNo(user.phone)){
            if(this.getUserByPhone(user.phone) != null) return null;
        }
        //如果存在有效的身份证号，则需要验证此号是否已经使用过
        if(user.idCardNo != null && !user.idCardNo.isEmpty() && MyString.isIdCardNo(user.idCardNo)){
            if(this.getUserByIdCardNo(user.idCardNo) != null) return null;
        }
        if(this.userModelMapper.updateById(user) > 0) return user;
        return null;
    }

    @Override
    public UserModel getUserById(String id) {
        if(!MyString.isUuid(id)) return null;
        return this.userModelMapper.selectById(id);
    }

    @Override
    public UserModel getUserByName(String name) {
        if(MyString.isEmptyOrNull(name)) return null;
        return this.userModelMapper.selectByName(name);
    }

    @Override
    public UserModel getUserByPhone(String phone) {
        if(!MyString.isPhoneNo(phone)) return null;
        return this.userModelMapper.selectByPhone(phone);
    }

    @Override
    public UserModel getUserByEmail(String email) {
        if(!MyString.isEmail(email)) return null;
        return this.userModelMapper.selectByEmail(email);
    }

    @Override
    public UserModel getUserByIdCardNo(String idCardNo) {
        if(!MyString.isIdCardNo(idCardNo)) return null;
        return this.userModelMapper.selectByIdCardNo(idCardNo);
    }

    @Override
    public List<UserModel> getUserByOrganizationId(String organizationId) {
        if(!MyString.isUuid(organizationId)) return null;
        return this.userModelMapper.selectByOrganizationId(organizationId);
    }
}
