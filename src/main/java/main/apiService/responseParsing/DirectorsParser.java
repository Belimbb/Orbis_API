package main.apiService.responseParsing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class DirectorsParser {
    public List<Map<String, String>> getFirstThreeDirectors(String jsonResponse) {
        List<Map<String, String>> allDirectors = parseApiResponse(jsonResponse, "firstThree");
        return allDirectors.stream().limit(3).collect(Collectors.toList());
    }
    public List<Map<String, String>> getAllDirectors(String jsonResponse) {
        return parseApiResponse(jsonResponse, "all");
    }

    private List<Map<String, String>> parseApiResponse(String jsonResponse, String typeOfResponse) {
        List<Map<String, String>> directorsInfo = new ArrayList<>();
        JsonElement jsonElement = JsonParser.parseString(jsonResponse);
        if (jsonElement.isJsonObject()) {
            JsonArray dataArray = jsonElement.getAsJsonObject().getAsJsonArray("Data");
            if (dataArray != null && !dataArray.isEmpty()) {
                if (typeOfResponse.equals("firstThree")){
                    dataArray.forEach(element -> processFirstThreeDirectorData(element.getAsJsonObject(), directorsInfo));
                }else {
                    dataArray.forEach(element -> processAllDirectorData(element.getAsJsonObject(), directorsInfo));
                }
            }
        }
        return directorsInfo;
    }

    private void processAllDirectorData(JsonObject dataObject, List<Map<String, String>> directorsInfo) {
        Map<String, String> directorInfo = new HashMap<>();
        String[] directorKeys = {"CPYCONTACTS_HEADER_FullNameOriginalLanguagePreferred", "CPYCONTACTS_HEADER_IdDirector",
                "CPYCONTACTS_MEMBERSHIP_Function", "CPYCONTACTS_MEMBERSHIP_CurrentPrevious",
                "CPYCONTACTS_HEADER_Birthdate", "CPYCONTACTS_HEADER_MultipleNationalitiesLabel",
                "CPYCONTACTS_MEMBERSHIP_IsAShareholderFormatted"};

        for (String key : directorKeys) {
            JsonElement element = dataObject.get(key);
            if (element != null && !element.isJsonNull()) {
                if (element.isJsonArray()) {
                    // Обработка массива
                    JsonArray array = element.getAsJsonArray();
                    directorInfo.put(key, StreamSupport.stream(array.spliterator(), false)
                            .filter(jsonElement -> !jsonElement.isJsonNull())
                            .map(JsonElement::getAsString)
                            .collect(Collectors.joining(", ")));
                } else if (element.isJsonPrimitive()) {
                    directorInfo.put(key, element.getAsString());
                }
            }
        }

        if (!directorInfo.isEmpty()) {
            directorsInfo.add(directorInfo);
        }
    }

    private void processFirstThreeDirectorData(JsonObject dataObject, List<Map<String, String>> directorsInfo) {
        Map<String, String> directorInfo = new HashMap<>();
        String[] directorKeys = {"CPYCONTACTS_HEADER_FullNameOriginalLanguagePreferred", "CPYCONTACTS_HEADER_IdDirector",
                "CPYCONTACTS_MEMBERSHIP_Function", "CPYCONTACTS_MEMBERSHIP_CurrentPrevious",
                "CPYCONTACTS_HEADER_Birthdate", "CPYCONTACTS_HEADER_MultipleNationalitiesLabel",
                "CPYCONTACTS_MEMBERSHIP_IsAShareholderFormatted"};
        for (String key : directorKeys) {
            JsonElement element = dataObject.get(key);
            if (element != null && !element.isJsonNull()) {
                if (element.isJsonArray()) {
                    JsonArray array = element.getAsJsonArray();
                    // Выбираем только первый элемент массива или объединяем их
                    String arrayValue = StreamSupport.stream(array.spliterator(), false)
                            .filter(jsonElement -> !jsonElement.isJsonNull())
                            .map(JsonElement::getAsString)
                            .findFirst()
                            .orElse("");
                    directorInfo.put(key, arrayValue);
                } else if (element.isJsonPrimitive()) {
                    directorInfo.put(key, element.getAsString());
                }
            }
        }
        if (!directorInfo.isEmpty()) {
            directorsInfo.add(directorInfo);
        }
    }

}

