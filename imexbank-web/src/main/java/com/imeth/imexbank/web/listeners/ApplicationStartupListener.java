package com.imeth.imexbank.web.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ApplicationStartupListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("ImexBank Application Starting Up...");
        // Application-wide initialization logic can be placed here
        // For example, setting up default system users or configuration
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("ImexBank Application Shutting Down...");
    }
}