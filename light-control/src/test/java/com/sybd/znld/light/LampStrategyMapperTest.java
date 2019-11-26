package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.LampStrategyMapper;
import com.sybd.znld.model.lamp.LampStrategyModel;
import com.sybd.znld.model.lamp.Strategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Assert;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LampStrategyMapperTest {
    @Autowired
    private LampStrategyMapper lampStrategyMapper;

    // 新增 异常
    @Test(expected = Exception.class)
    public void test(){
        var model = new LampStrategyModel();
        this.lampStrategyMapper.insert(model);
    }

    // 新增 照明灯策略
    @Test
    public void test1(){
        var now = LocalDate.now();
        var model = new LampStrategyModel();
        model.name = "照明灯策略";
        model.fromDate = now.plusDays(1);
        model.toDate = now.plusDays(30);
        model.fromTime = LocalTime.of(17,30,0);
        model.toTime = LocalTime.of(5, 30, 0);
        model.autoGenerateTime = false;
        model.type = Strategy.LAMP;
        var ret = this.lampStrategyMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 修改
    @Test
    public void test2(){
        var model = new LampStrategyModel();
        model.id = "5ba3ec3467ec4c8987cdaf4cbe65f095";
        model.type = Strategy.ELECTRICITY_DISPOSITION_BOX;
        var ret = this.lampStrategyMapper.update(model);
        Assert.assertTrue(ret > 0);
    }

    // 查询，根据id
    @Test
    public void test3(){
        var id = "5ba3ec3467ec4c8987cdaf4cbe65f095";
        var ret = this.lampStrategyMapper.selectById(id);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.id, id);
    }

    // 查询，根据名字
    @Test
    public void test4(){
        var name = "配电箱策略";
        var ret = this.lampStrategyMapper.selectByName(name);
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }
}
