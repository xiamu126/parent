package com.sybd.znld.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyServletContextListener implements ServletContextListener {

    private final Logger log = LoggerFactory.getLogger(MyServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.debug("context初始完毕");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.debug("context清理完毕");
    }
}
