package com.sybd.znld.lamp;

import com.sybd.znld.model.lamp.LampModel;
import com.sybd.znld.service.lamp.LampService;
import com.sybd.znld.service.lamp.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ServiceTest {
    private LampService lampService;
    private RegionService regionService;

    // 小岗村，新增路灯
    @Test
    public void test(){
        var lamp = new LampModel();
    }
}
