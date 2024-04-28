package main.ui;

import lombok.Getter;

import main.apiService.ApiService;
import main.apiService.responseParsing.ResponseParser;
import main.requests.MultiRequest;
import main.systemSettings.AppRegistry;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.*;
public class BotDialogHandler {
    private static final String START_PHOTO = "https://www.bvdinfo.com/en-gb/-/media/product-logos/orbis.png?h=592&iar=0&w=800&hash=FD098D9A01606D12046B2DD0C7972ADA";

    @Getter
    private final MessageFactory messageFactory;
    private final Long chatId;

    public BotDialogHandler(Long chatId) {
        this.messageFactory = new MessageFactory(chatId);
        this.chatId = chatId;
    }


    public SendPhoto createWelcomeMessage() {
        String caption = "<b>Welcome</b>. \n" + "This bot will help you get detailed information about the company";
        SendPhoto photoMessage = messageFactory.createPhotoMessage(START_PHOTO, caption);
        photoMessage.setReplyMarkup(ButtonFactory.getReplyKeyboardMarkup());
        return photoMessage;
    }
    public SendMessage createGetInfoMessage(){
        String text = "<b>Select a search option from this list</b>";
        SendMessage message = messageFactory.createMessage(text);
        message.setReplyMarkup(ButtonFactory.createKeyboardForCriteriaSelection(chatId, getInformationOptions(), "getInformation"));
        return message;
    }

    public SendMessage createMultiRequestMessage(){
        String basicText = "<b>Here is results of searching</b>";
        String firstURL = MultiRequest.getMultiRequestUrl();
        String query = MultiRequest.createMultiRequestQuery(AppRegistry.getUser(chatId).getAllSearchCriteria());

        ResponseParser responseParser = new ResponseParser(new ApiService(AppRegistry.getUser(chatId).getOrbisToken()));
        AppRegistry.getUser(chatId).setJsonResponse(responseParser.sendRequest(firstURL, query));

        // Преобразование JSON-ответа в Map<String, String>
        Map<String, String> apiResponse = responseParser.parseCompanySummary(AppRegistry.getUser(chatId).getJsonResponse());

        // Форматирование ответа
        String formattedResponse = formatCompanySummary(apiResponse);

        SendMessage message = messageFactory.createMessage(basicText + "\n" + formattedResponse);
        message.setReplyMarkup(ButtonFactory.createUniversalInlineKeyboard(getMultiRequestOptions()));
        return message;
    }

    public SendMessage createDirectorsMessage(){
        String basicText = "<b>Directors</b>";

        ResponseParser responseParser = new ResponseParser(new ApiService(AppRegistry.getUser(chatId).getOrbisToken()));

        // Преобразование JSON-ответа в Map<String, String>
        String jsonResponse = AppRegistry.getUser(chatId).getJsonResponse();
        List<Map<String, String>> apiResponse = responseParser.parseFirstThreeDirectors(jsonResponse);
        System.out.println("Arrays.toString(apiResponse.toArray()) = " + Arrays.toString(apiResponse.toArray()));
        // Форматирование ответа
        String formattedResponse = formatDirectorsSummary(apiResponse);
        System.out.println("formattedResponse = " + formattedResponse);

        SendMessage message = messageFactory.createMessage(basicText + "\n" + formattedResponse);
        message.setReplyMarkup(ButtonFactory.getReplyKeyboardMarkup());
        return message;
    }

    private String formatCompanySummary(Map<String, String> apiResponse) {
        StringBuilder formattedResponse = new StringBuilder();

        // Add each value from the API response
        formattedResponse.append("<b>BvdID:</b> ").append(apiResponse.getOrDefault("BVDID", "n/a")).append("\n");
        formattedResponse.append("<b>Name:</b> ").append(apiResponse.getOrDefault("NAME", "n/a")).append("\n");
        formattedResponse.append("<b>Email or Website:</b> ").append(apiResponse.getOrDefault("EmailOrWebsite", "n/a")).append("\n");

        // Formatting National ID and National ID Label
        String[] nationalIds = apiResponse.getOrDefault("NATIONAL_ID", "").split(",");
        String[] nationalIdLabels = apiResponse.getOrDefault("NATIONAL_ID_LABEL", "").split(",");
        formattedResponse.append("<b>National ID:</b> ");
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

        formattedResponse.append("<b>Country:</b> ").append(apiResponse.getOrDefault("COUNTRY", "n/a")).append("\n");
        formattedResponse.append("<b>Local name:</b> ").append(apiResponse.getOrDefault("Name_Local", "n/a")).append("\n");
        return formattedResponse.toString();

    }

    private String formatDirectorsSummary(List<Map<String, String>>  directors) {
        StringBuilder formattedResponse = new StringBuilder();

        // Assuming 'directors' is a list of maps where each map contains director's information
        for (Map<String, String> director : directors) {
            formattedResponse.append("<b>Full Name:</b> ").append(director.getOrDefault("CPYCONTACTS_HEADER_FullNameOriginalLanguagePreferred", "n/a")).append("\n");
            formattedResponse.append("<b>Id Director:</b> ").append(director.getOrDefault("CPYCONTACTS_HEADER_IdDirector", "n/a")).append("\n");
            formattedResponse.append("<b>Function:</b> ").append(director.getOrDefault("CPYCONTACTS_MEMBERSHIP_Function", "n/a")).append("\n");
            formattedResponse.append("<b>Current/Previous:</b> ").append(director.getOrDefault("CPYCONTACTS_MEMBERSHIP_CurrentPrevious", "n/a")).append("\n");

            String birthdate = director.getOrDefault("CPYCONTACTS_HEADER_Birthdate", "n/a").substring(0, 11);
            formattedResponse.append("<b>Birthdate:</b> ").append(birthdate).append("\n");

            //formattedResponse.append("<b>Birthdate:</b> ").append(director.getOrDefault("CPYCONTACTS_HEADER_Birthdate", "n/a")).append("\n");
            formattedResponse.append("<b>Nationalities:</b> ").append(director.getOrDefault("CPYCONTACTS_HEADER_MultipleNationalitiesLabel", "n/a")).append("\n");
            formattedResponse.append("<b>Shareholder:</b> ").append(director.getOrDefault("CPYCONTACTS_MEMBERSHIP_IsAShareholderFormatted", "n/a")).append("\n\n");
        }

        return formattedResponse.toString();
    }

    public SendMessage createSearchCriteriaForm() {
        Map<String, String> searchCriteria = AppRegistry.getUser(chatId).getAllSearchCriteria();
        StringBuilder formMessage = new StringBuilder("Please write information for search:\n");

        // For each key in the searchCriteria add a line to the form
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

        // Enumerating user-entered strings
        String[] lines = message.split("\n");
        for (String line : lines) {
            line = line.trim();
            for (String criteriaKey : searchCriteria.keySet()) {
                if (!line.isEmpty()) {
                    // Updates the search criterion if the string matches
                    searchCriteria.put(criteriaKey, line);
                    break;
                }
            }
        }

        AppRegistry.getUser(chatId).setAllSearchCriteria(searchCriteria);
    }

    public EditMessageText onInformMessage(Long chatId, Integer messageId) {
        String text = "<b>Select a search option from this list</b>";
        EditMessageText message = messageFactory.createEditMessage(chatId, messageId, text);
        message.setReplyMarkup(ButtonFactory.createKeyboardForCriteriaSelection(chatId, getInformationOptions(), "getInformation"));
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
    private Map<String, String> getMultiRequestOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("directors", "Directors");
        options.put("founder", "Founder");
        options.put("owner", "Beneficial owner");
        options.put("subsidiary", "Subsidiary companies");
        options.put("branches", "Branches");
        return options;
    }

    //Need rebuild
    private Map<String, String> getSettingsOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("bank", "Банки");
        options.put("currency", "Валюти");
        options.put("decimal", "Знаків після коми");
        options.put("notification", "Час сповіщення");
        return options;
    }
}
