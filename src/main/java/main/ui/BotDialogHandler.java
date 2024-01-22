package main.ui;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.*;
/**
 * MVC: View
 * @author AlekseyB belovmladshui@gmail.com
 */
public class BotDialogHandler {
    private static final String URL_MEDIA = "https://epowhost.com/currency_chat_bot";
    private final MessageFactory messageFactory;

    public BotDialogHandler(Long chatId) {
        this.messageFactory = new MessageFactory(chatId);
    }

    // Стартовое сообщение
    public SendPhoto createWelcomeMessage() {
        String caption = "<b>Ласкаво просимо.</b> \nЦей бот допоможе отримати детальну інформацію про компанії";
        SendPhoto photoMessage = messageFactory.createPhotoMessage(URL_MEDIA + "/welcome_message.jpg", caption);
        photoMessage.setReplyMarkup(ButtonFactory.getReplyKeyboardMarkup());
        return photoMessage;
    }

    public SendPhoto createAboutUsMessage() {
        String caption = "Розробник: <b>JavaCrafters Team</b>\nРепозиторій проєкту: https://github.com/vikadmin88/CurrencyChatBot";
        SendPhoto photoMessage = messageFactory.createPhotoMessage(URL_MEDIA + "/about_us.jpg", caption);
        photoMessage.setReplyMarkup(ButtonFactory.getReplyKeyboardMarkup());
        return photoMessage;
    }

    public SendMessage createSettingsMessage() {
        String text = "⚙ <b>Налаштування</b>";
        SendMessage message = messageFactory.createMessage(text);
        message.setReplyMarkup(ButtonFactory.getInlineKeyboardMarkup(getSettingsOptions(), "settings", new ArrayList<>()));
        return message;
    }

    public EditMessageText onDecimalMessage(Integer messageId) {
        String text = "<b>Знаків після коми</b>";
        return messageFactory.editMessage(messageId, text);
    }

    private Map<String, String> getSettingsOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("bank", "Банки");
        options.put("currency", "Валюти");
        options.put("decimal", "Знаків після коми");
        options.put("notification", "Час сповіщення");
        return options;
    }
}
