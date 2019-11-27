package com.sybd.znld.light;

import com.sybd.znld.light.control.dto.LampStrategy;
import com.sybd.znld.light.control.dto.NewBoxStrategy;
import com.sybd.znld.light.control.dto.NewLampStrategy;
import com.sybd.znld.light.service.IStrategyService;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StrategyServiceTest {
    @Autowired
    private IStrategyService strategyService;

    // 新增照明灯策略
    @Test
    public void test(){
        var strategy = new NewLampStrategy();
        strategy.name = "照明灯策略测试";
        strategy.userId = "a6b354d551f111e9804a0242ac110007";
        strategy.ids = List.of("123b93dd012142ee82deb3558d8df055");
        strategy.from = MyDateTime.toTimestamp(LocalDateTime.of(2019, 11,28,17,0,0));
        strategy.to = MyDateTime.toTimestamp(LocalDateTime.of(2019, 12,28,6,0,0));
        var point1 = new LampStrategy.Point();
        point1.time = MyDateTime.toTimestamp(LocalDateTime.of(2019, 12,28,4,0,0));
        point1.brightness = 80;
        var point2 = new LampStrategy.Point();
        point2.time = MyDateTime.toTimestamp(LocalDateTime.of(2019, 12,28,5,0,0));
        point2.brightness = 70;
        strategy.points = List.of(point1, point2);
        var ret = this.strategyService.newLampStrategy(strategy);
        Assert.assertTrue(ret);
    }

    // 新增配电箱策略
    @Test
    public void test2(){
        var strategy = new NewBoxStrategy();
        strategy.name = "配电箱策略测试";
        strategy.userId = "a6b354d551f111e9804a0242ac110007";
        strategy.ids = List.of("0e88236629de4bf4b97225b95d3f8f9f");
        strategy.from = MyDateTime.toTimestamp(LocalDateTime.of(2019, 11,28,17,0,0));
        strategy.to = MyDateTime.toTimestamp(LocalDateTime.of(2019, 12,28,6,0,0));
        var ret = this.strategyService.newBoxStrategy(strategy);
        Assert.assertTrue(ret);
    }
}
