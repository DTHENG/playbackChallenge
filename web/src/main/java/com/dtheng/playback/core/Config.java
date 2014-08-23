package com.dtheng.playback.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Daniel Thengvall
 */
public class Config {

    private static final Logger LOG = Logger.getLogger(Config.class.getSimpleName());

    public static String getProperty(String property) {
        return getProperties().getProperty(property);
    }

    public static Properties getProperties() {
        Properties properties = new Properties();
        URL resource = Config.class.getResource("/spela.properties");
        File propertiesFile = new File("./src/main/resources/spela.properties");
        try {
            propertiesFile = new File(resource.toURI());
        } catch (URISyntaxException e) {
            LOG.info(e.toString());
            LOG.info("Using default source path.");
        }
        try {
            FileInputStream stream = new FileInputStream(propertiesFile);
            properties.load(stream);
        } catch (IOException ioe) {
            LOG.info(ioe.toString());
        }
        return properties;
    }
}
