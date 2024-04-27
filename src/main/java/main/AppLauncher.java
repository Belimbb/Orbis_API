package main;

import main.systemSettings.AppRegistry;
import main.systemSettings.ConfigLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Company's (Owners)= https://api.bvdinfo.com/v1/orbis/Companies/data?query=
//Contacts (Directors) = https://api.bvdinfo.com/v1/orbis/contacts/data?query=
public class AppLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppLauncher.class);

    public static void main(String[] args) {
        LOGGER.info("{}", "*".repeat(40));
        LOGGER.info("App starting in Thread: {}", Thread.currentThread().getName());

        AppRegistry.initDefaults();

        String orbisToken = ConfigLoader.get("APP_ORBIS_TOKEN");
        String appName = ConfigLoader.get("APP_NAME");
        String botName = ConfigLoader.get("APP_BOT_NAME");
        String botToken = ConfigLoader.get("APP_BOT_TOKEN");

        ChatBot bot = new ChatBot(orbisToken, appName, botName, botToken);
        bot.botRun();
    }
}

