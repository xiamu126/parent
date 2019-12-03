package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.StrategyMapper;
import com.sybd.znld.model.Status;
import com.sybd.znld.model.lamp.StrategyModel;
import com.sybd.znld.model.lamp.Strategy;
import com.sybd.znld.model.onenet.OneNetKey;
import com.sybd.znld.model.onenet.dto.CommandParams;
import com.sybd.znld.service.onenet.IOneNetService;
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
public class StrategyMapperTest {
    @Autowired
    private StrategyMapper strategyMapper;
    @Autowired
    private IOneNetService oneNetService;

    // 新增 异常
    @Test(expected = Exception.class)
    public void test(){
        var model = new StrategyModel();
        this.strategyMapper.insert(model);
    }

    // 新增 照明灯策略
    @Test
    public void test1(){
        var now = LocalDate.now();
        var model = new StrategyModel();
        model.name = "照明灯策略";
        model.fromDate = now.plusDays(1);
        model.toDate = now.plusDays(30);
        model.fromTime = LocalTime.of(17,30,0);
        model.toTime = LocalTime.of(5, 30, 0);
        model.autoGenerateTime = false;
        model.type = Strategy.LAMP;
        var ret = this.strategyMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 修改
    @Test
    public void test2(){
        var model = new StrategyModel();
        model.id = "5ba3ec3467ec4c8987cdaf4cbe65f095";
        model.type = Strategy.ELECTRICITY_DISPOSITION_BOX;
        var ret = this.strategyMapper.update(model);
        Assert.assertTrue(ret > 0);
    }

    // 查询，根据id
    @Test
    public void test3(){
        var id = "5ba3ec3467ec4c8987cdaf4cbe65f095";
        var ret = this.strategyMapper.selectById(id);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.id, id);
    }

    // 查询，根据名字
    @Test
    public void test4(){
        var name = "配电箱策略";
        var ret = this.strategyMapper.selectByName(name);
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }

    // 查询，根据组织id
    @Test
    public void test5(){
        var id = "88cc4ad365d9493f85db160b336c8414";
        var ret = this.strategyMapper.selectByOrganId(id);
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }

    // 查询，根据组织id和策略类型
    @Test
    public void test6(){
        var id = "88cc4ad365d9493f85db160b336c8414";
        var ret = this.strategyMapper.selectByOrganIdType(id, Strategy.LAMP);
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }

    @Test
    public void test7(){
        var param = new CommandParams();
        param.imei = "868194030003265";
        param.oneNetKey = OneNetKey.from("3311_0_5706");
        param.command = "{\"s\":[{\"v\":1,\"t\":10},{\"v\":1,\"t\":10},{\"v\":1,\"t\":10}]}";
        var ret = this.oneNetService.execute(param);
        log.debug(ret.error+" "+ret.errno);
    }

    @Test
    public void test8(){
        var id = "88cc4ad365d9493f85db160b336c8414";
        var list = this.strategyMapper.selectByOrganIdTypeNotStatus(id, Strategy.LAMP, Status.LAMP_STRATEGY_EXPIRED);
        Assert.assertNotNull(list);
    }
}
