package main.apiService.responseParsing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CompanySummaryParser{

    public Map<String, String> parseApiResponse(String jsonResponse) {
        Map<String, String> apiResponse = new HashMap<>();
        Optional.ofNullable(JsonParser.parseString(jsonResponse))
                .map(JsonElement::getAsJsonObject)
                .map(jsonObject -> jsonObject.getAsJsonArray("Data"))
                .filter(dataArray -> !dataArray.isEmpty())
                .ifPresent(dataArray -> processFirstDataObject(dataArray.get(0).getAsJsonObject(), apiResponse));

        return apiResponse;
    }

    private void processFirstDataObject(JsonObject dataObject, Map<String, String> apiResponse) {
        dataObject.keySet().forEach(key -> {
            JsonElement valueElement = dataObject.get(key);
            if (valueElement.isJsonPrimitive()) {
                apiResponse.put(key, valueElement.getAsString());
            } else if (valueElement.isJsonArray()) {
                processJsonArray(valueElement.getAsJsonArray(), key, apiResponse);
            } else if (valueElement.isJsonObject() && key.equals("MATCH")) {
                processMatchObject(valueElement.getAsJsonObject(), apiResponse);
            }
        });
    }

    private void processJsonArray(JsonArray jsonArray, String key, Map<String, String> apiResponse) {
        String value = StreamSupport.stream(jsonArray.spliterator(), false)
                .filter(jsonElement -> !jsonElement.isJsonNull()) // Для фильтрации JsonNull элементов
                .map(JsonElement::getAsString)
                .collect(Collectors.joining(", "));
        apiResponse.put(key, value);
    }


    private void processMatchObject(JsonObject matchObject, Map<String, String> apiResponse) {
        matchObject.entrySet().stream()
                .filter(entry -> entry.getValue().isJsonObject())
                .forEach(entry -> {
                    JsonObject matchDetails = entry.getValue().getAsJsonObject();
                    matchDetails.keySet().forEach(detailKey ->
                            apiResponse.put(detailKey, matchDetails.get(detailKey).getAsString())
                    );
                });
    }
}

