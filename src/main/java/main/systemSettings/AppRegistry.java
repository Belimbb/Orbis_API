package main.systemSettings;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

import main.ChatBot;
import main.apiService.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppRegistry.class);
    @Getter
    @Setter
    private static ChatBot chatBot;
    private static final Map<Long, User> users = new HashMap<>();

    public static void initDefaults() {
        LOGGER.info("Initializing default settings.");

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

    // Configuration parameters
    public static String getConfVal(String key) {
        return ConfigLoader.get(key);
    }
}
