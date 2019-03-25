package com.sybd.znld.service.impl;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.BaseService;
import com.sybd.znld.service.rbac.mapper.UserMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
public class UserServiceImpl extends BaseService {
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public UserServiceImpl(UserMapper userMapper,
                           ModelMapper modelMapper,
                           CacheManager cacheManager,
                           TaskScheduler taskScheduler, ProjectConfig projectConfig) {
        super(cacheManager, taskScheduler, projectConfig);
        this.userMapper = userMapper;
        this.modelMapper = modelMapper;
    }

    //@Cacheable(key="#root.targetClass.getName()+'.user_'+#input.name", unless = "#result == null")
    //@Cacheable(key="#root.targetClass.getName()+'.user_'+#name")//不管结果如何都做缓存，以便面对一些恶意攻击的时候频繁访问数据库
/*    public UserEntity verify(String name, String password) {
        var ret = userMapper.verify(name, password);
        if(ret == null){
            this.removeCache(this.getClass(),".user_"+name, expirationTime);
        }
        return ret;
    }*/

    //@Cacheable(key="#root.targetClass.getName()+'.user_'+#id")//不管结果如何都做缓存，以便面对一些恶意访问的时候频繁访问数据库
    /*public UserEntity getUserById(String id) {
        if(id == null || id.equals("")){
            this.removeCache(this.getClass(),".user_null", expirationTime);
            return null;
        }
        if(!id.matches("[0-9a-f]{32}")){
            this.removeCache(this.getClass(),".user_"+id, expirationTime);
            return null;
        }
        UserEntity ret = userMapper.getUserById(id);
        if(ret == null){
            this.removeCache(this.getClass(),".user_"+id, expirationTime); //3分钟后失效
        }
        return ret;
    }*/

    //@Cacheable(key="#root.targetClass.getName()+'.user_'+#name")
    /*public UserEntity getUserByName(String name) {
        if(MyString.isEmptyOrNull(name)){
            this.removeCache(UserServiceImpl.class, ".user_null", expirationTime); //3分钟后失效
            return null;
        }
        UserEntity ret = userMapper.getUserByName(name);
        if(ret == null){
            this.removeCache(UserServiceImpl.class, ".user_"+name, expirationTime); //3分钟后失效
        }
        return ret;
    }*/

 /*   @CachePut(key="#root.targetClass.getName()+'.user_'+#input.id", unless = "#result == null")
    public UserEntity updateById(UserEntity input) {
        if(input == null || input.getId() == null || input.getId().equals("")){
            return null;
        }
        int ret = userMapper.updateById(input);
        if(ret <= 0) return null;
        return userMapper.getUserById(input.getId());
    }

    @CachePut(key="#root.targetClass.getName()+'.user_'+#input.name", unless = "#result == null")
    public UserEntity updateByName(UserEntity input) {
        if(input == null || input.getName() == null || input.getName().equals("")){
            return null;
        }
        int ret = userMapper.updateByName(input);
        if(ret <= 0) return null;
        return userMapper.getUserByName(input.getName());
    }

    //@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 30)
    //如果需要有自己单独的事务的则使用REQUIRES_NEW，这意味着在嵌套使用时，如果外层发生异常，不影响自己的正常执行
    @Cacheable(key="#root.targetClass.getName()+'.user_'+#input.name", unless = "#result == null")
    public UserEntity register(RegisterInput input) {
        if(userMapper.existsByName(input.getName()) == null){
            UserEntity ret = modelMapper.map(input, UserEntity.class);
            userMapper.add(ret);
            return ret;
        }
        return null;
    }*/
}
