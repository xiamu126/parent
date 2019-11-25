package com.sybd.znld.account.service;

import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.mapper.rbac.UserRoleMapper;
import com.sybd.znld.model.rbac.AuthorityModel;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.LoginInput;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserService implements IUserService {
    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;
    private final RegionMapper regionMapper;
    private final LampMapper lampMapper;
    private final LampResourceMapper lampResourceMapper;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final OrganizationMapper organizationMapper;
    private final LampRegionMapper lampRegionMapper;
    private final BCryptPasswordEncoder encoder;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public UserService(UserMapper userMapper,
                       UserRoleMapper userRoleMapper,
                       RegionMapper regionMapper,
                       LampMapper lampMapper,
                       LampResourceMapper lampResourceMapper,
                       OneNetResourceMapper oneNetResourceMapper,
                       OrganizationMapper organizationMapper,
                       LampRegionMapper lampRegionMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.regionMapper = regionMapper;
        this.lampMapper = lampMapper;
        this.lampResourceMapper = lampResourceMapper;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.organizationMapper = organizationMapper;
        this.lampRegionMapper = lampRegionMapper;
        this.encoder = new BCryptPasswordEncoder(10);
    }

    @Override
    public UserModel modifyUserById(UserModel user) {
        if(!MyString.isUuid(user.id)) return null;
        if(this.userMapper.updateById(user) > 0) {
            return this.userMapper.selectById(user.id);
        }
        return null;
    }

    @Override
    public UserModel modifyUserByName(UserModel user) {
        if(user == null || MyString.isEmptyOrNull(user.name)) return null;
        if(this.userMapper.updateByName(user) > 0) {
            return this.userMapper.selectByName(user.name);
        }
        return null;
    }

    @Override
    public UserModel getUserById(String id) {
        if(!MyString.isUuid(id)) return null;
        return this.userMapper.selectById(id);
    }

    @Override
    public UserModel getUserByName(String name) {
        if(MyString.isEmptyOrNull(name)) return null;
        return this.userMapper.selectByName(name);
    }

    @Override
    public UserModel getUserByPhone(String phone) {
        if(!MyString.isPhoneNo(phone)) return null;
        return this.userMapper.selectByPhone(phone);
    }

    @Override
    public UserModel getUserByEmail(String email) {
        if(!MyString.isEmail(email)) return null;
        return this.userMapper.selectByEmail(email);
    }

    @Override
    public UserModel getUserByIdCardNo(String idCardNo) {
        if(!MyString.isIdCardNo(idCardNo)) return null;
        return this.userMapper.selectByIdCardNo(idCardNo);
    }

    @Override
    public List<UserModel> getUserByOrganizationId(String organizationId) {
        if(!MyString.isUuid(organizationId)) return null;
        return this.userMapper.selectByOrganizationId(organizationId);
    }

    @Override
    public UserModel verify(String name, String password) {
        if(MyString.isAnyEmptyOrNull(name, password)) return null;
        var user = this.getUserByName(name);
        if(user != null && user.password.equals(password)){
            return user;
        }
        return null;
    }

    @Override
    public UserModel verify(LoginInput input) {
        if(input == null || !input.isValid()) return null;
        var user = this.getUserByName(input.user);
        if(user != null && user.password.equals(input.password)){
            return user;
        }
        return null;
    }

    @Override
    //@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public UserModel addUser(UserModel model) {
        if(model == null || !model.isValidForInsert()) return null;
        //名字不能重复
        //if(this.userMapper.selectByName(model.name) != null) return null;
        if(this.getUserByName(model.name) != null) return null;
        //手机号不能重复，如果存在的话
        //身份证号不能重复，如果存在的话
        //邮箱不能重复，如果存在的话
        //所属组织必须存在
        if(this.organizationMapper.selectById(model.organizationId) == null) return null;
        model.password = this.encoder.encode(model.password); // 密码加密后存储，统一在这里做，其它地方可以免去
        if(this.userMapper.insert(model) > 0) return model; else return null;
    }

    @Override
    public UserModel register(RegisterInput input) {
        if(input == null || !input.isValid()) return null;
        var user = new UserModel();
        user.name = input.name;
        user.password = input.password;
        user.organizationId = input.organizationId;
        user.lastLoginTime = LocalDateTime.now();
        user.status = 0;
        return addUser(user);
    }

    @Override
    public List<AuthorityModel> getAuthoritiesById(String userId) {
        return null;
    }
}
