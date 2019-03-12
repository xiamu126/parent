package com.sybd.znld.service.v2.rbac;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.v2.rbac.UserModel;
import com.sybd.znld.service.mapper.v2.UserModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public UserModel insertUser(UserModel model) {
        if(this.userModelMapper.insert(model) > 0) return model;
        else return null;
    }
}
