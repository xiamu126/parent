package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.StrategyTargetMapper;
import com.sybd.znld.model.lamp.StrategyTargetModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StrategyTargetMapperTest {
    @Autowired
    private StrategyTargetMapper strategyTargetMapper;

    // 新增 异常
    @Test(expected = Exception.class)
    public void test(){
        var model = new StrategyTargetModel();
        this.strategyTargetMapper.insert(model);
    }

    // 新增
    @Test
    public void test1(){
        var model = new StrategyTargetModel();
        model.targetId = "10bb23399d3611e995980242c0a8b008";
        model.strategyId = "c1034d8e20024bcbb1a6623f8c9a4bdc";
        var ret = this.strategyTargetMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 修改
    @Test
    public void test2(){
        var model = new StrategyTargetModel();
        model.id = "89a5dc4a56ea46349e9bf46b4c8bb03a";
        model.targetId = "3a991b715c3911e98edc0242ac110007";
        var ret = this.strategyTargetMapper.update(model);
        Assert.assertTrue(ret > 0);
    }

    // 查询，根据id
    @Test
    public void test3(){
        var id = "89a5dc4a56ea46349e9bf46b4c8bb03a";
        var ret = this.strategyTargetMapper.selectById(id);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.id, id);
    }

    // 查询，根据策略id
    @Test
    public void test4(){
        var id = "c1034d8e20024bcbb1a6623f8c9a4bdc";
        var ret = this.strategyTargetMapper.selectByStrategyId(id);
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }
}
