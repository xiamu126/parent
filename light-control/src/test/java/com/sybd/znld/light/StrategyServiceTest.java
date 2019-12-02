package com.sybd.znld.light;

import com.sybd.znld.light.controller.dto.BaseStrategy;
import com.sybd.znld.light.controller.dto.BoxStrategy;
import com.sybd.znld.light.controller.dto.LampStrategy;
import com.sybd.znld.light.controller.dto.StrategyTarget;
import com.sybd.znld.light.service.IStrategyService;
import com.sybd.znld.model.lamp.Target;
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
        var strategy = new LampStrategy();
        strategy.name = "照明灯策略测试";
        strategy.userId = "a6b354d551f111e9804a0242ac110007";
        var t = new StrategyTarget();
        t.ids = List.of("123b93dd012142ee82deb3558d8df055");
        t.target = Target.SINGLE;
        strategy.targets = List.of(t);
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
        var strategy = new BoxStrategy();
        strategy.name = "配电箱策略测试";
        strategy.userId = "a6b354d551f111e9804a0242ac110007";
        var t = new StrategyTarget();
        t.ids = List.of("0e88236629de4bf4b97225b95d3f8f9f");
        t.target = Target.SINGLE;
        strategy.targets = List.of(t);
        strategy.from = MyDateTime.toTimestamp(LocalDateTime.of(2019, 11,28,17,0,0));
        strategy.to = MyDateTime.toTimestamp(LocalDateTime.of(2019, 12,28,6,0,0));
        var ret = this.strategyService.newBoxStrategy(strategy);
        Assert.assertTrue(ret);
    }

    // 获取某个组织下的所有照明灯策略的具体情况
    @Test
    public void test3(){
        var ret = this.strategyService.getLampStrategies("88cc4ad365d9493f85db160b336c8414");
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }

    // 获取某个组织下的所有配电箱策略的具体情况
    @Test
    public void test4(){
        var ret = this.strategyService.getBoxStrategies("88cc4ad365d9493f85db160b336c8414");
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }

    // 测试下发照明灯指令
    @Test
    public void test5(){
        var strategy = new LampStrategy();
        var strategyTarget = new StrategyTarget();
        strategyTarget.ids = List.of("10bb23399d3611e995980242c0a8b008", "3a991b715c3911e98edc0242ac110007", "3acb80545c3911e98edc0242ac110007");
        strategyTarget.target = Target.SINGLE;
        strategy.targets = List.of();
        this.strategyService.newLampStrategy(strategy);
    }
}
