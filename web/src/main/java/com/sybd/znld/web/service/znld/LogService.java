package com.sybd.znld.web.service.znld;

import com.sybd.znld.mapper.lamp.HttpLogMapper;
import com.sybd.znld.model.http.HttpMethod;
import com.sybd.znld.model.lamp.HttpLogModel;
import com.sybd.znld.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogService implements ILogService {
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
        var tmp = httpLogMapper.selectByAll(model);
        if(tmp != null && !tmp.isEmpty()) return null;
        if(httpLogMapper.insert(model) > 0) {
            return model;
        }
        return null;
    }
}
