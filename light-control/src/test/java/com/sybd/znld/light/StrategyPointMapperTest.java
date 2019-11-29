package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.StrategyPointMapper;
import com.sybd.znld.model.lamp.StrategyPointModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StrategyPointMapperTest {
    @Autowired
    private StrategyPointMapper strategyPointMapper;

    // 新增 异常
    @Test(expected = Exception.class)
    public void test(){
        var model = new StrategyPointModel();
        this.strategyPointMapper.insert(model);
    }

    // 新增
    @Test
    public void test1(){
        var model = new StrategyPointModel();
        model.at = LocalTime.of(5,0,0);
        model.brightness = 70;
        model.strategyId = "c1034d8e20024bcbb1a6623f8c9a4bdc";
        var ret = this.strategyPointMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 修改
    @Test
    public void test2(){
        var model = new StrategyPointModel();
        model.id = "34c714cfc7854151b419cf82ac9471a6";
        model.brightness = 80;
        var ret = this.strategyPointMapper.update(model);
        Assert.assertTrue(ret > 0);
        Assert.assertEquals(80, (int) model.brightness);
    }

    // 查询，根据id
    @Test
    public void test3(){
        var id = "34c714cfc7854151b419cf82ac9471a6";
        var ret = this.strategyPointMapper.selectById(id);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.id, id);
    }

    // 查询，根据策略id
    @Test
    public void test4(){
        var id = "c1034d8e20024bcbb1a6623f8c9a4bdc";
        var ret = this.strategyPointMapper.selectByStrategyId(id);
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }
}
