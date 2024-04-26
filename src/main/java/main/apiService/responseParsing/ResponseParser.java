package main.apiService.responseParsing;

import main.apiService.ApiService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ResponseParser {
    private final ApiService apiService;

    public ResponseParser(ApiService apiService) {
        this.apiService = apiService;
    }

    public String sendRequest(String firstPartUrl, String query) {
        String fullUrl = firstPartUrl + URLEncoder.encode(query, StandardCharsets.UTF_8);
        return apiService.getJsonResponse(fullUrl);
    }

    public Map<String, String> parseCompanySummary(String jsonResponse) {
        CompanySummaryParser summaryParser = new CompanySummaryParser();
        return summaryParser.parseApiResponse(jsonResponse);
    }
    public List<Map<String, String>> parseAllDirectors(String jsonResponse) {
        DirectorsParser summaryParser = new DirectorsParser();
        return summaryParser.getAllDirectors(jsonResponse);
    }
    public List<Map<String, String>> parseFirstThreeDirectors(String jsonResponse) {
        DirectorsParser summaryParser = new DirectorsParser();
        return summaryParser.getFirstThreeDirectors(jsonResponse);
    }

}

