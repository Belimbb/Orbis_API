package main.ui;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import main.ChatBot;
import main.apiService.ApiService;
import main.apiService.ResponseParser;
import main.apiService.User;
import main.requests.MultiRequest;
import main.systemSettings.AppRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.lang.reflect.Type;
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
    public SendMessage createMultiRequestMessage(){
        String basicText = "Here is results of searching";
        String firstURL = MultiRequest.getMultiRequestUrl();
        String query = MultiRequest.createMultiRequestQuery(AppRegistry.getUser(chatId).getAllSearchCriteria());

        ResponseParser responseParser = new ResponseParser(new ApiService(AppRegistry.getUser(chatId).getToken()));
        String response = responseParser.sendRequest(firstURL, query);

        SendMessage message = messageFactory.createMessage(basicText+"\n"+response);
        message.setReplyMarkup(ButtonFactory.getReplyKeyboardMarkup());
        return message;
    }

 */

    public SendMessage createMultiRequestMessage(){
        String basicText = "Here is results of searching";
        String firstURL = MultiRequest.getMultiRequestUrl();
        String query = MultiRequest.createMultiRequestQuery(AppRegistry.getUser(chatId).getAllSearchCriteria());

        ResponseParser responseParser = new ResponseParser(new ApiService(AppRegistry.getUser(chatId).getToken()));
        String response = responseParser.sendRequest(firstURL, query);

        // Преобразование JSON-ответа в Map<String, String>
        Map<String, String> apiResponse = parseApiResponse(response);

        // Форматирование ответа
        String formattedResponse = formatResponse(apiResponse);

        SendMessage message = messageFactory.createMessage(basicText + "\n" + formattedResponse);
        message.setReplyMarkup(ButtonFactory.getReplyKeyboardMarkup());
        return message;
    }
    private Map<String, String> parseApiResponse(String jsonResponse) {
        Map<String, String> apiResponse = new HashMap<>();
        JsonElement jsonElement = JsonParser.parseString(jsonResponse);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonArray dataArray = jsonObject.getAsJsonArray("Data");
            if (dataArray != null && !dataArray.isEmpty()) {
                JsonObject dataObject = dataArray.get(0).getAsJsonObject();
                for (String key : dataObject.keySet()) {
                    JsonElement valueElement = dataObject.get(key);
                    if (valueElement.isJsonPrimitive()) {
                        apiResponse.put(key, valueElement.getAsString());
                    } else if (valueElement.isJsonArray()) {
                        // Обработка массивов, например, NATIONAL_ID и NATIONAL_ID_LABEL
                        JsonArray array = valueElement.getAsJsonArray();
                        StringBuilder value = new StringBuilder();
                        for (int i = 0; i < array.size(); i++) {
                            value.append(array.get(i).getAsString());
                            if (i < array.size() - 1) value.append(", ");
                        }
                        apiResponse.put(key, value.toString());
                    } else if (valueElement.isJsonObject() && key.equals("MATCH")) {
                        JsonObject matchObject = valueElement.getAsJsonObject();
                        for (String matchKey : matchObject.keySet()) {
                            JsonElement matchValue = matchObject.get(matchKey);
                            if (matchValue.isJsonObject()) {
                                JsonObject matchDetails = matchValue.getAsJsonObject();
                                for (String detailKey : matchDetails.keySet()) {
                                    apiResponse.put(detailKey, matchDetails.get(detailKey).getAsString());
                                }
                            }
                        }
                    }
                }
            }
        }
        return apiResponse;
    }




    private String formatResponse(Map<String, String> apiResponse) {
        StringBuilder formattedResponse = new StringBuilder();

        // Добавляем каждое значение из ответа API
        formattedResponse.append("BvdID: ").append(apiResponse.getOrDefault("BVDID", "n/a")).append("\n");
        formattedResponse.append("Name: ").append(apiResponse.getOrDefault("NAME", "n/a")).append("\n");
        formattedResponse.append("Email or Website: ").append(apiResponse.getOrDefault("EmailOrWebsite", "n/a")).append("\n");

        // Форматирование National ID и National ID Label
        String[] nationalIds = apiResponse.getOrDefault("NATIONAL_ID", "").split(",");
        String[] nationalIdLabels = apiResponse.getOrDefault("NATIONAL_ID_LABEL", "").split(",");
        formattedResponse.append("National ID: ");
        for (int i = 0; i < nationalIds.length; i++) {
            if (i < nationalIdLabels.length) {
                formattedResponse.append(nationalIds[i].trim()).append(" (").append(nationalIdLabels[i].trim()).append(")");
            } else {
                formattedResponse.append(nationalIds[i].trim()).append(" (n/a)");
            }
            if (i < nationalIds.length - 1) {
                formattedResponse.append(", ");
            }
        }
        formattedResponse.append("\n");

        formattedResponse.append("Country : ").append(apiResponse.getOrDefault("COUNTRY", "n/a")).append("\n");
        formattedResponse.append("Overview full overview: ").append(apiResponse.getOrDefault("OVERVIEW_FULL_OVERVIEW", "n/a")).append("\n");
        formattedResponse.append("Local name: ").append(apiResponse.getOrDefault("Match.Name_Local", "n/a")).append("\n");

        // ... и так далее для других полей

        return formattedResponse.toString();
    }    public SendMessage createSearchCriteriaForm() {
        Map<String, String> searchCriteria = AppRegistry.getUser(chatId).getAllSearchCriteria();
        StringBuilder formMessage = new StringBuilder("Please write information for search:\n");

        // Для каждого ключа в searchCriteria добавляем строку в форму
        for (String criteriaKey : searchCriteria.keySet()) {
            if (getInformationOptions().containsKey(criteriaKey)) {
                String criteriaName = getInformationOptions().get(criteriaKey);
                formMessage.append(criteriaName).append(":\n");
            }
        }
        SendMessage message = messageFactory.createMessage(formMessage.toString());
        message.setReplyMarkup(ButtonFactory.getReplyKeyboardMarkup());
        return message;
    }
    public void updateSearchCriteriaFromMessage(String message) {
        Map<String, String> searchCriteria = AppRegistry.getUser(chatId).getAllSearchCriteria();

        // Перебор введенных пользователем строк
        String[] lines = message.split("\n");
        for (String line : lines) {
            line = line.trim();
            for (String criteriaKey : searchCriteria.keySet()) {
                if (!line.isEmpty()) {
                    // Обновление критерия поиска, если строка соответствует
                    searchCriteria.put(criteriaKey, line);
                    break; // Выход из цикла после обновления
                }
            }
        }

        AppRegistry.getUser(chatId).setAllSearchCriteria(searchCriteria);
    }

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
