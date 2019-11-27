package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.LampLampModuleMapper;
import com.sybd.znld.model.lamp.LampLampModuleModel;
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
public class LampLampModuleMapperTest {
    @Autowired
    private LampLampModuleMapper lampLampModuleMapper;

    // 新增，异常
    @Test(expected = Exception.class)
    public void test(){
        var model = new LampLampModuleModel();
        this.lampLampModuleMapper.insert(model);
    }

    // 新增
    @Test
    public void test1(){
        var model = new LampLampModuleModel();
        model.lampId = "10bb23399d3611e995980242c0a8b008";
        model.lampModuleId = 1;
        var ret = this.lampLampModuleMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 修改
    @Test
    public void test2(){
        var model = new LampLampModuleModel();
        model.id = "ac8e8f28ada94fbcb84ebd021edb07b3";
        model.lampModuleId = 2;
        var ret = this.lampLampModuleMapper.update(model);
        Assert.assertTrue(ret > 0);
    }

    // 查询，根据id
    @Test
    public void test3(){
        var id = "ac8e8f28ada94fbcb84ebd021edb07b3";
        var ret = this.lampLampModuleMapper.selectById(id);
        Assert.assertNotNull(ret);
    }

    // 查询，根据路灯id
    @Test
    public void test4(){
        var id = "10bb23399d3611e995980242c0a8b008";
        var ret = this.lampLampModuleMapper.selectByLampId(id);
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }
}
