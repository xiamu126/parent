package com.sybd.znld.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConditionalOnClass(Greeter.class)
@EnableConfigurationProperties(ProjectConfig.class)
public class ProjectAutoConfiguration {
}
