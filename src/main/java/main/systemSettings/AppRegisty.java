package main.systemSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import main.ChatBot;
import main.apiService.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppRegistry.class);

    private static ChatBot chatBot;
    private static final Map<Long, User> users = new HashMap<>();
    private static NetworkClient netClient;

    public static String getConfVal(String key) {
        return ConfigLoader.get(key);
    }

    private AppRegistry() {
    }

    public static void initDefaults() {
        LOGGER.info("Loading config defaults: initDefaults()");

        Date dateTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String platformTime = dateFormat.format(dateTime);
        dateFormat.setTimeZone(TimeZone.getTimeZone(getConfTimeZone()));

        LOGGER.info("Runtime mode: >>>>> {} <<<<<", getConfIsDevMode() ? "DEV" : "PROD");
        LOGGER.info("App time zone: {}, App time {}, Platform time {}", getConfTimeZone(), dateFormat.format(dateTime), platformTime);

        // user-storage folder, provider
        if (Boolean.parseBoolean(ConfigLoader.get("APP_USERS_USE_STORAGE"))) {
            try {
                String storageFolder = getConfUsersStorageFolder();
                Files.createDirectories(Paths.get(storageFolder));
                if (ConfigLoader.get("APP_USERS_STORAGE_PROVIDER").equals("file")) {
                    UserLoader.setStorageProvider(new JsonStorageProvider(storageFolder)).load();
                    LOGGER.info("User-storage provider ({}) added: JsonStorageProvider()", ConfigLoader.get("APP_USERS_STORAGE_PROVIDER"));
                } else if (ConfigLoader.get("APP_USERS_STORAGE_PROVIDER").equals("sqlite")) {
                    UserLoader.setStorageProvider(new SQLiteStorageProvider(storageFolder)).load();
                    LOGGER.info("User-storage provider ({}) added: SQLiteStorageProvider()", ConfigLoader.get("APP_USERS_STORAGE_PROVIDER"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
        ChatBot
        */
    public static void setChatBot(ChatBot bot){
        chatBot = bot;
    }
    public static ChatBot getChatBot(){
        return chatBot;
    }


    /*
    Users
    */
    public static void addUser(User user){
        users.put(user.getId(), user);
    }
    public static void addUserCompletely(User user){
        LOGGER.info("addUserCompletely: {} {}", user.getId(), user.getName());
        addUser(user);
    }
    public static Map<Long, User>getUsers(){
        return new HashMap<Long, User>(users);
    }
    public static User getUser(Long userId){
        return users.get(userId);
    }
    public static boolean hasUser(Long userId){
        return users.get(userId) != null;
    }
    public static void removeUser(Long userId){
        users.remove(userId);
    }
    public static void removeUserCompletely(Long userId){
        LOGGER.info("removeUserCompletely: {}", userId);
        removeUser(userId);
        UserLoader.delete(userId);
    }
}
