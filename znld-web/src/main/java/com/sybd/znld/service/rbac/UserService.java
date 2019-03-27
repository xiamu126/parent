package com.sybd.znld.service.rbac;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.rbac.AuthorityModel;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.service.BaseService;
import com.sybd.znld.service.rbac.dto.LoginInput;
import com.sybd.znld.service.rbac.dto.RegisterInput;
import com.sybd.znld.service.rbac.mapper.OrganizationMapper;
import com.sybd.znld.service.rbac.mapper.UserMapper;
import com.sybd.znld.util.MyString;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
@Service
@DbSource("rbac")
public class UserService extends BaseService implements IUserService {
    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;
    private final BCryptPasswordEncoder encoder;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public UserService(UserMapper userMapper,
                       CacheManager cacheManager, TaskScheduler taskScheduler, ProjectConfig projectConfig, OrganizationMapper organizationMapper) {
        super(cacheManager, taskScheduler, projectConfig);
        this.userMapper = userMapper;
        this.organizationMapper = organizationMapper;
        this.encoder = new BCryptPasswordEncoder(10);
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
        if(this.userMapper.updateById(user) > 0) return user;
        return null;
    }

    @Override
    public UserModel modifyUserByName(UserModel model) {
        if(model == null || MyString.isEmptyOrNull(model.name)) return null;
        if(this.userMapper.updateByName(model) > 0) return model;
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
        return this.userMapper.selectByNameAndPassword(name, password);
    }

    @Override
    public UserModel verify(LoginInput input) {
        if(input == null || !input.isValid()) return null;
        return this.userMapper.selectByNameAndPassword(input.user, input.password);
    }

    @Override
    //@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public UserModel addUser(UserModel model) {
        if(model == null || !model.isValid()) return null;
        //名字不能重复
        if(this.userMapper.selectByName(model.name) != null) return null;
        //手机号不能重复，如果存在的话
        //身份证号不能重复，如果存在的话
        //邮箱不能重复，如果存在的话
        //所属组织必须存在
        if(this.organizationMapper.selectById(model.organizationId) == null) return null;
        if(this.userMapper.insert(model) > 0) return model; else return null;
    }

    @Override
    public UserModel register(RegisterInput input) {
        if(input == null || !input.isValid()) return null;
        var propertyMap = new PropertyMap<RegisterInput, UserModel>(){
            @Override
            protected void configure() {
                map(source.name, destination.name);
                map(source.password, destination.password);
                map(source.organizationId, destination.organizationId);
                map(destination.lastLoginTime).setLastLoginTime(LocalDateTime.now());
                map(destination.status).setStatus((short)0);
                skip(destination.phone);
                skip(destination.email);
                skip(destination.gender);
                skip(destination.age);
                skip(destination.contactAddress);
                skip(destination.realName);
                skip(destination.idCardNo);
                skip(destination.lastLoginIp);
            }
        };
        var modelMapper = new ModelMapper();
        modelMapper.addMappings(propertyMap);
        modelMapper.validate();
        var user = modelMapper.map(input, UserModel.class);
        return addUser(user);
    }

    @Override
    public List<AuthorityModel> getAuthoritiesById(String userId) {
        return null;
    }
}
