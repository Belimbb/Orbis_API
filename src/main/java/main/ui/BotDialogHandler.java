package main.ui;

import main.ChatBot;
import main.apiService.User;
import main.systemSettings.AppRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.*;
/**
 * MVC: View
 * @author AlekseyB belovmladshui@gmail.com
 */
public class BotDialogHandler {
    private static final String START_PHOTO = "https://www.bvdinfo.com/en-gb/-/media/product-logos/orbis.png?h=592&iar=0&w=800&hash=FD098D9A01606D12046B2DD0C7972ADA";
    private final MessageFactory messageFactory;
    private Long chatId;

    public BotDialogHandler(Long chatId) {
        this.messageFactory = new MessageFactory(chatId);
        this.chatId = chatId;
    }

    // Стартовое сообщение
    public SendPhoto createWelcomeMessage() {
        String caption = "<b>Welcome</b>. \n" + "This bot will help you get detailed information about the company";
        SendPhoto photoMessage = messageFactory.createPhotoMessage(START_PHOTO, caption);
        photoMessage.setReplyMarkup(ButtonFactory.getReplyKeyboardMarkup());
        return photoMessage;
    }
    public SendMessage createGetInfoMessage(){
        String text = "Select a search option from this list";
        SendMessage message = messageFactory.createMessage(text);
        message.setReplyMarkup(ButtonFactory.getInlineKeyboardMarkup(chatId, getInformationOptions(), "getInformation"));
        return message;
    }
/*
    public SendPhoto createAboutUsMessage() {
        String caption = "Розробник: <b>JavaCrafters Team</b>\nРепозиторій проєкту: https://github.com/vikadmin88/CurrencyChatBot";
        SendPhoto photoMessage = messageFactory.createPhotoMessage(URL_MEDIA + "/about_us.jpg", caption);
        photoMessage.setReplyMarkup(ButtonFactory.getReplyKeyboardMarkup());
        return photoMessage;
    }
*/
    public SendMessage createSettingsMessage() {
        String text = "⚙ <b>Settings</b>";
        SendMessage message = messageFactory.createMessage(text);
        message.setReplyMarkup(ButtonFactory.getInlineKeyboardMarkup(chatId, getSettingsOptions(), "settings"));
        return message;
    }
    public EditMessageText onInformMessage(Long chatId, Integer messageId) {
        String text = "<b>Select a search option from this list</b>";
        EditMessageText message = messageFactory.createEditMessage(chatId, messageId, text);
        message.setReplyMarkup(ButtonFactory.getInlineKeyboardMarkup(chatId, getInformationOptions(), "getInformation"));
        return message;
    }
    private Map<String, String> getInformationOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("name", "Name");
        options.put("city", "City");
        options.put("country", "Country");
        options.put("address", "Address");
        options.put("emailOrWebsite", "Email or Website");
        options.put("nationalId", "National Id");
        options.put("phoneOrFax", "Phone or Fax");
        options.put("ticker", "Ticker");
        options.put("orbisId", "Orbis ID");
        options.put("submit", "Submit criteria");
        return options;
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
