package com.sybd.znld.service.rbac;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.mapper.rbac.BaseUserMapper;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.mapper.rbac.UserRoleMapper;
import com.sybd.znld.model.lamp.LampRegionModel;
import com.sybd.znld.model.lamp.LampResourceModel;
import com.sybd.znld.model.lamp.OneNetResourceModel;
import com.sybd.znld.model.lamp.RegionModel;
import com.sybd.znld.model.rbac.AuthorityModel;
import com.sybd.znld.model.rbac.OrganizationModel;
import com.sybd.znld.model.rbac.RoleModel;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.model.rbac.dto.AuthPackByUser;
import com.sybd.znld.model.rbac.dto.LoginInput;
import com.sybd.znld.model.rbac.dto.InitAccountInput;
import com.sybd.znld.model.rbac.dto.RegisterInput;
import com.sybd.znld.service.BaseService;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
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
                       LampRegionMapper lampRegionMapper,
                       RedissonClient redissonClient,
                       ObjectMapper objectMapper) {
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
        //return this.userMapper.selectByNameAndPassword(name, password);
    }

    @Override
    public UserModel verify(LoginInput input) {
        if(input == null || !input.isValid()) return null;
        var user = this.getUserByName(input.user);
        if(user != null && user.password.equals(input.password)){
            return user;
        }
        return null;
        //return this.userMapper.selectByNameAndPassword(input.user, input.password);
    }

    @Override
    //@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public UserModel addUser(UserModel model) {
        if(model == null || !model.isValidForInsert()) return null;
        //名字不能重复
        if(this.getUserByName(model.name) != null) return null;
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
        return addUser(user);
    }

    @Override
    public List<AuthorityModel> getAuthoritiesById(String userId) {
        return null;
    }

    @Override
    public void initAccount(InitAccountInput input) {

        // 新建组织
        // 关联此账号所属组织
        // 关联此组织所对应的oauth2信息
        var organ = this.organizationMapper.selectByName(input.organizationName);
        if(organ == null){
            organ = new OrganizationModel();
            organ.oauth2ClientId = input.oauth2ClientId;
            organ.name= input.organizationName;
            this.organizationMapper.insert(organ);
        }

        var user = this.userMapper.selectByName(input.user.name);
        if(user == null){
            input.user.organizationId = organ.id;
            this.addUser(input.user);
        }

        // 新建区域
        var region = this.regionMapper.selectByName(input.regionName);
        if(region == null){
            region = new RegionModel();
            region.name = input.regionName;
            region.organizationId = organ.id;
            this.regionMapper.insert(region);
        }

        // 新建路灯，并关联资源，关联区域和路灯
        var resources = this.oneNetResourceMapper.selectByResourceType(OneNetResourceModel.Type.Value);
        for(var l : input.lamps){
            var lamp = this.lampMapper.selectByDeviceId(l.deviceId);
            if(lamp == null){
                this.lampMapper.insert(l);
                lamp = l;
            }
            var lampRegion = this.lampRegionMapper.selectByLampIdAndRegionId(lamp.id, region.id);
            if(lampRegion == null){
                var lampRegionModel = new LampRegionModel();
                lampRegionModel.lampId = lamp.id;
                lampRegionModel.regionId = region.id;
                this.lampRegionMapper.insert(lampRegionModel);
            }
            for(var r : resources){
                var lr = this.lampResourceMapper.selectByLampIdAndResourceId(lamp.id, r.id);
                if(lr == null){
                    var tmp = new LampResourceModel();
                    tmp.oneNetResourceId = r.id;
                    tmp.lampId = l.id;
                    this.lampResourceMapper.insert(tmp);
                }
            }
        }
    }
}
