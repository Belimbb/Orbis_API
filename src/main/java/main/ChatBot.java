package main;

import main.apiService.User;
import main.systemSettings.AppRegistry;

import main.ui.BotDialogHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;

import static main.systemSettings.AppRegistry.removeUser;

public class ChatBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatBot.class);
    private final String appName;
    private final String botName;
    private final String botToken;
    private HashMap<String, String> commandMap;

    public ChatBot(String appName, String botName, String botToken) {
        this.appName = appName;
        this.botName = botName;
        this.botToken = botToken;
    }
    @Override
    public String getBotUsername() { return this.botName; }
    @Override
    public String getBotToken() { return this.botToken; }
    public void botRun() {
        TelegramBotsApi api;
        try {
            api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        AppRegistry.setChatBot(this);
    }

    public Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        return null;
    }

    private void addUser(Long chatId, Update update) {
        if (chatId == null) {
            return;
        }
        String firstName = "";
        String userName = "";
        if (update.hasMessage()) {
            firstName = update.getMessage().getFrom().getFirstName();
            userName = update.getMessage().getFrom().getLastName();
        }
        if (update.hasCallbackQuery()) {
            firstName = update.getCallbackQuery().getFrom().getFirstName();
            userName = update.getCallbackQuery().getFrom().getLastName();
        }
        User user = new User(chatId, firstName, userName);
        user.setToken("2LK951a1674f439eee11abd50278abee30dc");
        LOGGER.info("addUser: {} {}", chatId, firstName);
        AppRegistry.addUser(user);
    }
    private void checkOrAddUser(Long chatId, Update update) {
        if (!AppRegistry.hasUser(chatId)) {
            addUser(chatId, update);
        }
    }
    private BotDialogHandler getDH(Long chatId) {
        return new BotDialogHandler(chatId);
    }
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = getChatId(update);
        // check or add user
        checkOrAddUser(chatId, update);

        // Messages processing
        if (update.hasMessage()) {
            String messageText = update.getMessage().getText();

            String msgCommand = update.getMessage().getText();
            LOGGER.info("onUpdate: msgCommand: {}  User: {} {}", msgCommand, chatId, AppRegistry.getUser(chatId).getName());

            // Start
            if (msgCommand.equals("/start")) {
                doCommandStart(chatId);
            }
            else if (msgCommand.equals("/get_information") || msgCommand.endsWith("Get Information")){
                doCommandGetInformation(chatId);
            } else {
                SendMessage waitMessage = getDH(chatId).getMessageFactory().createMessage("Processing your request...");
                sendMessage(waitMessage);
                getDH(chatId).updateSearchCriteriaFromMessage(messageText);
                sendMessage(getDH(chatId).createMultiRequestMessage());
            }
        }
        // Callbacks processing
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();

            String[] btnCommand = data.split("_");
            LOGGER.info("btnCommand: {} btnCommand[] {}  User: {} {}", data,
                    Arrays.toString(btnCommand), chatId, AppRegistry.getUser(chatId).getName());
            if (btnCommand[0].toLowerCase().equals("getinformation")){
                doCallMultiRequest(chatId, update, btnCommand);
            }else {
                switch (btnCommand[0].toLowerCase()) {
                    case "directors" -> doDirectorsRequest(chatId);
                }
            }
        }
    }
    /*
     *
     * Message Commands
     * */
    public void doCommandStart(Long chatId) {
        SendPhoto ms = getDH(chatId).createWelcomeMessage();
        try {
            execute(ms);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public void doCommandGetInformation(Long chatId) {
        SendMessage ms = getDH(chatId).createGetInfoMessage();
        try {
            execute(ms);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void doCallMultiRequest(Long chatId, Update update, String[] command) {
        User user = AppRegistry.getUser(chatId);
        if (!command[1].equals("submit")) {
            if (user.getAllSearchCriteria().containsKey(command[1])){
                user.getAllSearchCriteria().remove(command[1]);
            }else {
                user.addSearchCriteria(command[1], "");
            }
            // Отправка сообщения в чат
            EditMessageText ms = getDH(chatId).onInformMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            sendMessage(ms);

        }else {
            // Отправка сообщения в чат - форма для ввода данных
            SendMessage ms = getDH(chatId).createSearchCriteriaForm();
            sendMessage(ms);
        }
    }

    private void doDirectorsRequest(Long chatId){
        SendMessage ms = getDH(chatId).createDirectorsMessage();
        sendMessage(ms);
    }

    public void sendMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                if (e.getMessage().contains("[403] Forbidden")) {
                    LOGGER.error("Can't sent sendMessage() Error message: {}", e.getMessage());
                    LOGGER.info("User {} left chat, removing...", message.getChatId());
                    removeUser(Long.parseLong(message.getChatId()));
                } else {
                    LOGGER.error("Can't sendMessage() sendMessage", e);
                }
            }
        }
    }

    public void sendMessage(EditMessageText message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                LOGGER.error("Can't sendMessage() EditMessageText", e);
            }
        }
    }
}
