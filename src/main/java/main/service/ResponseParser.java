package main.service;

import com.google.gson.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ResponseParser {
    private static final String COMPANIES_URL = "https://api.bvdinfo.com/v1/orbis/Companies/data?query=";
    private static final String CONTACTS_URL = "https://api.bvdinfo.com/v1/orbis/contacts/data?query=";
    private final ApiService apiService;

    public ResponseParser(ApiService apiService) {
        this.apiService = apiService;
    }

    public String sendRequest(String requestType, String query) {
        String fullUrl = getBaseUrl(requestType) + URLEncoder.encode(query, StandardCharsets.UTF_8);
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
    private String getBaseUrl(String requestType) {
        return switch (requestType.toLowerCase()) {
            case "contacts" -> CONTACTS_URL;
            case "companies" -> COMPANIES_URL;
            default -> throw new IllegalArgumentException("Unknown request type: " + requestType);
        };
    }
}

