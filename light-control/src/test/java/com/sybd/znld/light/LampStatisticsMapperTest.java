package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.LampStatisticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LampStatisticsMapperTest {
    @Autowired
    private LampStatisticsMapper lampStatisticsMapper;

    @Test
    public void test() {
        var ret = this.lampStatisticsMapper.selectLastDayByOrganId("88cc4ad365d9493f85db160b336c8414");
        log.debug(String.valueOf(ret.size()));
    }
}
