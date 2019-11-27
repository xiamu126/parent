package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.LampModuleMapper;
import com.sybd.znld.model.lamp.LampModuleModel;
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
public class LampModuleMapperTest {
    @Autowired
    private LampModuleMapper lampModuleMapper;

    // 新增，异常
    @Test(expected = Exception.class)
    public void test(){
        var model = new LampModuleModel();
        this.lampModuleMapper.insert(model);
    }

    //新增
    @Test
    public void test1(){
        var model = new LampModuleModel();
        model.name = "test";
        var ret = this.lampModuleMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 修改
    @Test
    public void test2(){
        var model = new LampModuleModel();
        model.id = 13;
        model.name = "test3";
        var ret = this.lampModuleMapper.update(model);
        Assert.assertTrue(ret > 0);
        Assert.assertEquals(model.name, "test3");
    }

    // 查询，根据id
    @Test
    public void test3(){
        var ret = this.lampModuleMapper.selectById(1);
        Assert.assertNotNull(ret);
    }

    // 查询，根据名字
    @Test
    public void test4(){
        var ret = this.lampModuleMapper.selectByName("环境监测");
        Assert.assertNotNull(ret);
    }
}
