package com.sybd.znld.config;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@Slf4j
public class MyServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.debug("context初始完毕");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.debug("context清理完毕");
    }
}
