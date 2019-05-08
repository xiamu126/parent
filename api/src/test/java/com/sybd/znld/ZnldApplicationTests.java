package com.sybd.znld;

import com.sybd.znld.model.onenet.OneNetKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZnldApplicationTests {
    private final Logger log = LoggerFactory.getLogger(ZnldApplicationTests.class);
    @Test
    public void contextLoads() {
        OneNetKey oneNetKey = OneNetKey.from("1_2_3");
        log.debug(oneNetKey.toString());

        Matcher matcher = Pattern.compile("(?!/api/v1/user/login).*").matcher("/api/v1/user/");
        log.debug(Boolean.toString(matcher.matches()));
    }
}
