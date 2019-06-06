package com.sybd.znld.web.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
