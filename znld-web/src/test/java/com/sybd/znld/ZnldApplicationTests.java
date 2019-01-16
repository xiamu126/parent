package com.sybd.znld;

import com.sybd.znld.onenet.dto.OneNetKey;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ZnldApplicationTests {

    @Test
    public void contextLoads() {
        var oneNetKey = OneNetKey.from("1_2_3");
        log.debug(oneNetKey.toString());

        var matcher = Pattern.compile("(?!/api/v1/user/login).*").matcher("/api/v1/user/");
        log.debug(Boolean.toString(matcher.matches()));
    }
}
