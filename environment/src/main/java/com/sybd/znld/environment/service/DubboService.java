package com.sybd.znld.environment.service;

import com.sybd.znld.service.IDubboService;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "1.0.0")
public class DubboService implements IDubboService {
    @Override
    public String sayHello(String name) {
        return "hello";
    }
}
