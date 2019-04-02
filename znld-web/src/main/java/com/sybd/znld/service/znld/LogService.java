package com.sybd.znld.service.znld;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.znld.HttpLogModel;
import com.sybd.znld.model.znld.HttpLogModel.HttpMethod;
import com.sybd.znld.service.znld.mapper.HttpLogMapper;
import com.sybd.znld.util.MyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService implements ILogService {
    private final Logger log = LoggerFactory.getLogger(LogService.class);
    private final HttpLogMapper httpLogMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public LogService(HttpLogMapper httpLogMapper) {
        this.httpLogMapper = httpLogMapper;
    }

    @Override
    public HttpLogModel addLog(HttpLogModel model) {
        if(model == null || MyString.isAnyEmptyOrNull(model.path, model.method, model.ip) || !HttpMethod.isValid(model.method)){
            return null;
        }
        if(httpLogMapper.selectByAll(model) != null) return null;
        if(httpLogMapper.insert(model) > 0) return model;
        return null;
    }
}