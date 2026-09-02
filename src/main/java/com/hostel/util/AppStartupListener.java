package com.hostel.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("HostelHub application started.");
        
        DatabaseInitializer.initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("HostelHub application stopped.");
    }
}