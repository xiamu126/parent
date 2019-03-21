package com.sybd.znld.service.znld;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.db.DbSource;
import com.sybd.znld.onenet.dto.OneNetExecuteParams;
import com.sybd.znld.onenet.dto.OneNetKey;
import com.sybd.znld.service.BaseService;
import com.sybd.znld.service.znld.mapper.ExecuteCommandMapper;
import com.sybd.znld.service.znld.model.ExecuteCommandModel;
import com.whatever.util.MyNumber;
import com.whatever.util.MyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
@Service
@DbSource("znld")
public class ExecuteCommandService extends BaseService implements IExecuteCommandService {
    private final Logger log = LoggerFactory.getLogger(ExecuteCommandService.class);
    private final ExecuteCommandMapper executeCommandMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ExecuteCommandService(ExecuteCommandMapper executeCommandMapper,
                                 CacheManager cacheManager, TaskScheduler taskScheduler, ProjectConfig projectConfig) {
        super(cacheManager, taskScheduler, projectConfig);
        this.executeCommandMapper = executeCommandMapper;
    }

    @Override
    public ExecuteCommandModel getExecuteCommandById(Integer id) {
        if(id == null || id <= 0) return null;
        return this.executeCommandMapper.selectById(id);
    }

    @Override
    public ExecuteCommandModel getExecuteCommandByValue(String value) {
        if(MyString.isEmptyOrNull(value)) return null;
        return this.executeCommandMapper.selectByValue(value);
    }

    @Override
    @Cacheable(key="#root.targetClass.getName()+'.getOneNetKeyByCommand@'+#cmd")
    public OneNetKey getOneNetKeyByCommand(String cmd, boolean evict) {
        if(evict){
            this.removeCache(this.getClass(),".getOneNetKeyByCommand@"+cmd);
            return null;
        }

        if(MyString.isEmptyOrNull(cmd)) return null;
        var tmp = this.executeCommandMapper.selectByValue(cmd);
        if(tmp == null) return null;
        var oneNetKey = new OneNetKey();
        oneNetKey.objId = tmp.objId;
        oneNetKey.objInstId = tmp.objInstId;
        oneNetKey.resId = tmp.resId;
        return oneNetKey;
    }

    @Override
    @Cacheable(key="#root.targetClass.getName()+'.getParamsByCommand@'+#cmd")
    public OneNetExecuteParams getParamsByCommand(String cmd, boolean evict) {
        if(evict){
            this.removeCache(this.getClass(),".getParamsByCommand@"+cmd);
            return null;
        }

        if(MyString.isEmptyOrNull(cmd)) return null;
        var entity = this.executeCommandMapper.selectByValue(cmd);
        if(entity == null) return null;
        var oneNetKey = getOneNetKeyByCommand(cmd, false);
        if(oneNetKey == null) return null;
        return new OneNetExecuteParams(oneNetKey, entity.timeout);
    }
}
