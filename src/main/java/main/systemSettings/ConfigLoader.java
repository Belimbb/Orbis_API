package main.systemSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);
    private static Properties conf = null;

    public static Properties getConf() {
        if (conf == null) {
            conf = loadConfig();
        }
        return conf;
    }

    public static String get(String key) {
        if (conf != null && !conf.isEmpty()) {
            return (String) conf.get(key);
        }
        conf = loadConfig();
        return (String) conf.get(key);
    }

    private static Properties loadConfig() {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("./app.properties")) {
            prop.load(input);
        } catch (IOException ex) {
            LOGGER.error("Can't find config file", ex);
            System.out.println(""" 
                    Config file ./app.properties not found! Please rename file ./app.properties-example to ./app.properties
                    and configure it like this:
                    # Bot credentials:
                    APP_NAME=OrbisBot
                    APP_BOT_NAME=here your bot name
                    APP_BOT_TOKEN=here your bot token
                                       
                    # User storage functionality:
                    # Enable/disable. save/load to/from json, database, etc.
                    APP_USERS_USE_STORAGE=true
                    # storage folder
                    APP_USERS_STORAGE_FOLDER=./bot-users
                    # storage provider. file | sqlite
                    APP_USERS_STORAGE_PROVIDER=sqlite
                    """);
            System.exit(1);
        }
        return prop;
    }
}
