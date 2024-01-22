package main.systemSettings;

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

    private AppRegistry() {
    }

    public static void initDefaults() {
        LOGGER.info("Initializing default settings.");

    }

    // ChatBot
    public static void setChatBot(ChatBot bot) {
        chatBot = bot;
    }

    public static ChatBot getChatBot() {
        return chatBot;
    }

    // Users
    public static void addUser(User user) {
        users.put(user.getId(), user);
    }

    public static User getUser(Long userId) {
        return users.get(userId);
    }

    public static boolean hasUser(Long userId) {
        return users.containsKey(userId);
    }

    public static void removeUser(Long userId) {
        users.remove(userId);
    }

    public static Map<Long, User> getUsers() {
        return new HashMap<>(users);
    }

    // Конфигурационные параметры
    public static String getConfVal(String key) {
        return ConfigLoader.get(key);
    }
}
