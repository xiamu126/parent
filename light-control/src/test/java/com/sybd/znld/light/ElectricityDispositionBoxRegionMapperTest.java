package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.ElectricityDispositionBoxMapper;
import com.sybd.znld.mapper.lamp.ElectricityDispositionBoxRegionMapper;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxModel;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxRegionModel;
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
public class ElectricityDispositionBoxRegionMapperTest {
    @Autowired
    private ElectricityDispositionBoxRegionMapper electricityDispositionBoxRegionMapper;

    // 新增，错误输入
    @Test(expected = Exception.class)
    public void test(){
        var model = new ElectricityDispositionBoxRegionModel();
        this.electricityDispositionBoxRegionMapper.insert(model);
    }

    // 新增
    @Test
    public void test1(){
        var model = new ElectricityDispositionBoxRegionModel();
        model.electricityDispositionBoxId = "08a9cf07fd27406faf298c9770c29dfc";
        model.regionId = "d4db3d36cbb843ca863b46153954b8d0";
        var ret = this.electricityDispositionBoxRegionMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 修改
    @Test
    public void test2(){
        var model = new ElectricityDispositionBoxRegionModel();
        model.id = "77e5f33bcfce45e8b2894de2e4ad149d";
        model.electricityDispositionBoxId = "08a9cf07fd27406faf298c9770c29dfc";
        model.regionId = "d4db3d36cbb843ca863b46153954b8d0";
        var ret = this.electricityDispositionBoxRegionMapper.update(model);
        Assert.assertTrue(ret > 0);
    }

    // 根据id查询
    @Test
    public void test3(){
        var id = "77e5f33bcfce45e8b2894de2e4ad149d";
        var ret = this.electricityDispositionBoxRegionMapper.selectById(id);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.id, id);
    }

    // 根据区域id查询
    @Test
    public void test4(){
        var id = "d4db3d36cbb843ca863b46153954b8d0";
        var ret = this.electricityDispositionBoxRegionMapper.selectByRegionId(id);
        Assert.assertNotNull(ret);
        Assert.assertTrue(ret.size() > 0);
    }

    // 根据配电箱id查询
    @Test
    public void test5(){
        var id = "08a9cf07fd27406faf298c9770c29dfc";
        var ret = this.electricityDispositionBoxRegionMapper.selectByBoxId(id);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.electricityDispositionBoxId, id);
    }
}
