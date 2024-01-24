package main.apiService;

import com.google.gson.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ResponseParser {
    private final ApiService apiService;

    public ResponseParser(ApiService apiService) {
        this.apiService = apiService;
    }

    public String sendRequest(String firstPartUrl, String query) {
        String fullUrl = firstPartUrl + URLEncoder.encode(query, StandardCharsets.UTF_8);
        return apiService.getJsonResponse(fullUrl);
    }

    public ArrayList<String> parseBvDIDs(String jsonResponse) {
        ArrayList<String> bvdIds = new ArrayList<>();
        JsonElement jsonElement = JsonParser.parseString(jsonResponse);

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonArray dataArray = jsonObject.getAsJsonArray("Data");
            if (dataArray != null) {
                for (JsonElement dataElement : dataArray) {
                    JsonObject dataObject = dataElement.getAsJsonObject();
                    String bvdId = dataObject.get("BVDID").getAsString();
                    bvdIds.add(bvdId);
                }
            }
        }
        return bvdIds;
    }
}

