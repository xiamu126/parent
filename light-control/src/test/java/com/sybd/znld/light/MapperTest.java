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
public class MapperTest {
    @Autowired
    private ElectricityDispositionBoxMapper electricityDispositionBoxMapper;

    @Test(expected = Exception.class)
    public void test(){
        var model = new ElectricityDispositionBoxModel();
        model.name = "测试配电箱";
        this.electricityDispositionBoxMapper.insert(model);
    }

    @Test
    public void test2(){
        var model = new ElectricityDispositionBoxModel();
        model.name = "测试配电箱";
        model.apiKey = "test api key";
        model.deviceId = "test device id";
        model.imei = "test imei";
        var ret = this.electricityDispositionBoxMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test3(){
        var model = new ElectricityDispositionBoxModel();
        model.name = "测试配电箱";
        model.apiKey = "test api key";
        model.deviceId = "test device id";
        model.imei = "test imei";
        model.rawLatitude = 37.4207605;
        model.rawLongitude = 122.16780126;
        var ret = this.electricityDispositionBoxMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void test4(){
        var model = new ElectricityDispositionBoxModel();
        model.name = "测试配电箱";
        model.apiKey = "test api key";
        model.deviceId = "test device id";
        model.imei = "test imei";
        model.rawLatitude = 37.4207605;
        model.rawLongitude = 122.16780126;
        model.lat = 37.4207605;
        model.lng = 122.16780126;
        var ret = this.electricityDispositionBoxMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }
}
