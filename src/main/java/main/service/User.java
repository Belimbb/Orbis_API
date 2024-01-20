package main.service;


public class User {
    private String token;
    private ApiService apiService;
    private ResponseParser responseParser;

    public User(String token) {
        this.token = token;
        this.apiService = new ApiService(token);
        this.responseParser = new ResponseParser(apiService);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ApiService getApiService() {
        return apiService;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public ResponseParser getResponseParser() {
        return responseParser;
    }

    public void setResponseParser(ResponseParser responseParser) {
        this.responseParser = responseParser;
    }
}
