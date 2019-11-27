package com.sybd.znld.light;

import com.sybd.znld.mapper.lamp.ElectricityDispositionBoxMapper;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxModel;
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
public class ElectricityDispositionBoxMapperTest {
    @Autowired
    private ElectricityDispositionBoxMapper electricityDispositionBoxMapper;

    // 新增，错误输入
    @Test(expected = Exception.class)
    public void test(){
        var model = new ElectricityDispositionBoxModel();
        model.name = "测试配电箱";
        this.electricityDispositionBoxMapper.insert(model);
    }

    // 新增，正常输入
    @Test
    public void test2(){
        var model = new ElectricityDispositionBoxModel();
        model.name = "测试配电箱";
        model.apiKey = "test api key";
        model.deviceId = 20002;
        model.imei = "test imei";
        var ret = this.electricityDispositionBoxMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 新增，正常输入，加上经纬度
    @Test
    public void test3(){
        var model = new ElectricityDispositionBoxModel();
        model.name = "测试配电箱";
        model.apiKey = "test api key";
        model.deviceId = 20003;
        model.imei = "test imei";
        model.rawLatitude = 37.4207605;
        model.rawLongitude = 122.16780126;
        var ret = this.electricityDispositionBoxMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 新增，正常输入，加上经纬度
    @Test
    public void test4(){
        var model = new ElectricityDispositionBoxModel();
        model.name = "测试配电箱";
        model.apiKey = "test api key";
        model.deviceId = 20004;
        model.imei = "test imei";
        model.rawLatitude = 37.4207605;
        model.rawLongitude = 122.16780126;
        model.lat = 37.4207605;
        model.lng = 122.16780126;
        var ret = this.electricityDispositionBoxMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    // 查询，根据id
    @Test
    public void test5(){
        var id = "08a9cf07fd27406faf298c9770c29dfc";
        var ret = this.electricityDispositionBoxMapper.selectById(id);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.id, id);
    }

    // 查询，根据name
    @Test
    public void test6(){
        var name = "测试配电箱";
        var ret = this.electricityDispositionBoxMapper.selectByName(name);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.name, name);
    }

    // 修改，根据id
    @Test
    public void test7(){
        var model = new ElectricityDispositionBoxModel();
        model.id = "08a9cf07fd27406faf298c9770c29dfc";
        model.name = "测试配电箱";
        model.apiKey = "test api key";
        model.deviceId = 20005;
        model.imei = "imei";
        model.rawLatitude = 0.0;
        model.rawLongitude = 0.0;
        model.lat = 0.0;
        model.lng = 0.0;
        var ret = this.electricityDispositionBoxMapper.update(model);
        Assert.assertTrue(ret > 0);
    }
}
