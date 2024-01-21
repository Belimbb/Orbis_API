package main.apiService;


import java.util.Objects;

public class User {
    private String token;
    private ApiService apiService;
    private ResponseParser responseParser;
    private Long id;
    private String name;
    private String username;

    public User(String token, Long id, String name, String username) {
        this.token = token;
        this.apiService = new ApiService(token);
        this.responseParser = new ResponseParser(apiService);
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ResponseParser getResponseParser() {
        return responseParser;
    }

    public void setResponseParser(ResponseParser responseParser) {
        this.responseParser = responseParser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(token, user.token) && Objects.equals(apiService, user.apiService)
                && Objects.equals(responseParser, user.responseParser)
                && Objects.equals(id, user.id) && Objects.equals(name, user.name)
                && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, apiService, responseParser, id, name, username);
    }
}
