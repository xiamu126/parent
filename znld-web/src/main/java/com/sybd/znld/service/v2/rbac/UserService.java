package com.sybd.znld.service.v2.rbac;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.v2.rbac.UserModel;
import com.sybd.znld.service.mapper.v2.UserModelMapper;
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
    public UserModel addUser(UserModel user) {
        var tmp = this.userModelMapper.selectByName(user.name);
        if(tmp != null) return null;
        if(this.userModelMapper.insert(user) > 0) return user;
        else return null;
    }

    @Override
    public UserModel modifyUser(UserModel user) {
        if(user.id == null || user.id.equals("") || !user.id.matches("^[0-9a-zA-Z]{32}$")) return null;
        if(user.phone != null && !user.phone.isEmpty() &&
                !user.phone.matches("^1\\d{10}$"))
            return null;
        if(this.userModelMapper.updateById(user) > 0) return user;
        return null;
    }

    @Override
    public UserModel getUserById(String id) {
        return this.userModelMapper.selectById(id);
    }

    @Override
    public UserModel getUserByName(String name) {
        return this.userModelMapper.selectByName(name);
    }

    @Override
    public UserModel getUserByPhone(String phone) {
        return this.userModelMapper.selectByPhone(phone);
    }

    @Override
    public UserModel getUserByEmail(String email) {
        return this.userModelMapper.selectByEmail(email);
    }

    @Override
    public UserModel getUserByIdCardNo(String idCardNo) {
        return this.userModelMapper.selectByIdCardNo(idCardNo);
    }

    @Override
    public List<UserModel> getUserByOrganizationId(String organizationId) {
        return this.userModelMapper.selectByOrganizationId(organizationId);
    }
}
