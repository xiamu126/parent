package com.sybd.znld.service.znld;

import com.sybd.znld.service.znld.mapper.LampMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LampService implements ILampService {
    private final Logger log = LoggerFactory.getLogger(LampService.class);
    private final LampMapper lampMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public LampService(LampMapper lampMapper) {
        this.lampMapper = lampMapper;
    }
}
