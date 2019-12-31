package com.sybd.znld.onenet.service;

import com.sybd.znld.model.environment.RawData;
import com.sybd.znld.model.lamp.dto.LampOnOffLine;
import com.sybd.znld.model.lamp.dto.LampStatistic;
import com.sybd.znld.model.onenet.dto.News;

import java.util.List;

public interface IMessageService {
    News onOff(String body);
    LampOnOffLine onOffLine(String body);
    News position(String body);
    News angle(String body);
    News environment(String body);
    LampStatistic statistics(String body);
    void scheduledStatistics();
}
