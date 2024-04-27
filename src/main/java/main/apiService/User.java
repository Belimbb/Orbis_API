package main.apiService;

import lombok.Data;
import main.apiService.responseParsing.ResponseParser;

import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    private String orbisToken;
    private ApiService apiService;
    private ResponseParser responseParser;
    private Long id;
    private String name;
    private String username;
    private String jsonResponse;
    private Map<String, String> searchCriteria = new HashMap<>();
    public void addSearchCriteria(String key, String value) {
        searchCriteria.put(key, value);
    }

    public String getSearchCriteria(String key) {
        return searchCriteria.getOrDefault(key, "");
    }
    public void setAllSearchCriteria(Map<String, String> searchCriteria){
        this.searchCriteria = searchCriteria;
    }

    public Map<String, String> getAllSearchCriteria() {
        return searchCriteria;
    }

    public User(Long id, String name, String username) {
        this.apiService = new ApiService(orbisToken);
        this.responseParser = new ResponseParser(apiService);
        this.id = id;
        this.name = name;
        this.username = username;
    }
}
