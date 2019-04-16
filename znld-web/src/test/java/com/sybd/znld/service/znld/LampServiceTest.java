package com.sybd.znld.service.znld;

import com.sybd.znld.model.LampModel;
import com.sybd.znld.model.OneNetResourceModel;
import com.sybd.znld.service.znld.mapper.LampMapper;
import com.sybd.znld.service.znld.mapper.OneNetResourceMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LampServiceTest {
    private final Logger log = LoggerFactory.getLogger(LampServiceTest.class);
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private ILampService lampService;
    @Autowired
    private LampMapper lampMapper;
    @Autowired
    private OneNetResourceMapper oneNetResourceMapper;

    @Test
    public void test(){
       var ret = this.lampService.getCheckedResourceByDeviceId(520914939);
        Assert.assertTrue(ret != null && !ret.isEmpty());
    }

    @Test
    public void getBoundResource(){
        var ret = this.lampMapper.selectBoundResourceByDeviceId(520914939);
        Assert.assertTrue(ret != null && !ret.isEmpty());
    }

    @Test
    public void addResource(){
        var model = new OneNetResourceModel();
        model.objId = 3201;
        model.objInstId = 0;
        model.resId = 5716;
        model.description = "中控重启指令";
        model.value = "205";
        model.type = OneNetResourceModel.Type.Command;
        if(!model.isValidBeforeInsert()) Assert.fail();
        var ret = this.oneNetResourceMapper.insert(model);
        Assert.assertTrue(ret > 0);
    }

    @Test
    public void addLampToRegion(){
        var model1 = new LampModel();
        model1.deviceId = 522756040;
        model1.deviceName = "路灯0004";
        model1.apiKey = "fN8PGSJ3VoIOSoznGWuGeC25PGY=";
        model1.imei = "868194030003265";

        var model2 = new LampModel();
        model2.deviceId = 522756075;
        model2.deviceName = "路灯0005";
        model2.apiKey = "fN8PGSJ3VoIOSoznGWuGeC25PGY=";
        model2.imei = "868194030006128";

        var ret = this.oneNetResourceMapper.selectByResourceType(OneNetResourceModel.Type.Value);
        Assert.assertTrue(ret != null && !ret.isEmpty());

        var list = ret.stream().map(resource -> resource.id).collect(Collectors.toList());

        var ret1 = this.lampService.addLampToRegion(model1, "62fa3afb56a111e98edc0242ac110007", list);
        var ret2 = this.lampService.addLampToRegion(model2, "62fa3afb56a111e98edc0242ac110007", list);
        Assert.assertNotNull(ret1);
        Assert.assertNotNull(ret2);
    }
}
