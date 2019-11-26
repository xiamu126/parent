package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.ElectricityDispositionBoxLampMapper;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxLampModel;
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
public class ElectricityDispositionBoxLampMapperTest {
    @Autowired
    private ElectricityDispositionBoxLampMapper electricityDispositionBoxLampMapper;

    // 新增，异常
    @Test(expected = Exception.class)
    public void test(){
        var model = new ElectricityDispositionBoxLampModel();
        this.electricityDispositionBoxLampMapper.insert(model);
    }

    // 新增
    @Test
    public void test1(){
        var model = new ElectricityDispositionBoxLampModel();
        model.electricityDispositionBoxId = "08a9cf07fd27406faf298c9770c29dfc";
        model.lampId = "3acb80545c3911e98edc0242ac110007";
        var ret = this.electricityDispositionBoxLampMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 修改
    @Test
    public void test2(){
        var model = new ElectricityDispositionBoxLampModel();
        model.id = "0d866330f7fc46b1b5bccfa616371be4";
        model.electricityDispositionBoxId = "08a9cf07fd27406faf298c9770c29dfc";
        model.lampId = "3acb80545c3911e98edc0242ac110008";
        var ret = this.electricityDispositionBoxLampMapper.update(model);
        Assert.assertTrue(ret > 0);
    }

    // 查询，根据id
    @Test
    public void test3(){
        var id = "0d866330f7fc46b1b5bccfa616371be4";
        var ret = this.electricityDispositionBoxLampMapper.selectById(id);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.id, id);
    }

    // 查询，根据配电箱id
    @Test
    public void test4(){
        var id = "08a9cf07fd27406faf298c9770c29dfc";
        var ret = this.electricityDispositionBoxLampMapper.selectByBoxId(id);
        Assert.assertNotNull(ret);
        Assert.assertFalse(ret.isEmpty());
    }
}
