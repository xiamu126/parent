package com.sybd.security.oauth2.server.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.sybd.security.oauth2.server.mapper")
public class MyBatisPlusConfig {
}
