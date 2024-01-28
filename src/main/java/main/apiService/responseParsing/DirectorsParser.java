package main.apiService.responseParsing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;


public class DirectorsParser {

    public List<Map<String, String>> parseApiResponse(String jsonResponse) {
        List<Map<String, String>> directorsInfo = new ArrayList<>();
        JsonElement jsonElement = JsonParser.parseString(jsonResponse);
        if (jsonElement.isJsonObject()) {
            JsonArray dataArray = jsonElement.getAsJsonObject().getAsJsonArray("Data");
            if (dataArray != null && !dataArray.isEmpty()) {
                dataArray.forEach(element -> processDirectorData(element.getAsJsonObject(), directorsInfo));
            }
        }
        return directorsInfo;
    }

    private void processDirectorData(JsonObject dataObject, List<Map<String, String>> directorsInfo) {
        Map<String, String> directorInfo = new HashMap<>();
        dataObject.keySet().forEach(key -> {
            JsonElement valueElement = dataObject.get(key);
            if (!valueElement.isJsonNull()) {
                if (valueElement.isJsonPrimitive()) {
                    directorInfo.put(key, valueElement.getAsString());
                }
            }
        });
        if (!directorInfo.isEmpty()) {
            directorsInfo.add(directorInfo);
        }
    }
}

