package com.arcanix.jcompass.web;

import com.arcanix.jcompass.CompassCompiler;
import com.arcanix.jcompass.CompassWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public class CompassServletContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompassServletContextListener.class);

    private CompassWatcher compassWatcher;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String configFilename = servletContextEvent.getServletContext().getInitParameter("jcompass.configFile");
        LOGGER.info("jCompass watcher started for: " + configFilename);
        String realPath = servletContextEvent.getServletContext().getRealPath(configFilename);
        CompassCompiler compassCompiler = new CompassCompiler(new File(realPath));
        this.compassWatcher = new CompassWatcher(compassCompiler);
        this.compassWatcher.watch();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (this.compassWatcher != null) {
            LOGGER.info("Stopping jCompass watcher...");
            this.compassWatcher.stop();
            LOGGER.info("jCompass watcher stopped");
        }
    }

}
