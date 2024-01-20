package main.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiService {
    private final OkHttpClient client;
    private final String token;

    public ApiService(String apiToken) {
        this.token = apiToken;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public String getJsonResponse(String apiUrl) {
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("ApiToken", token)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            JsonElement jsonElement = JsonParser.parseString(response.body().string());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(jsonElement);
        } catch (IOException e) {
            e.printStackTrace(); // В реальном приложении рекомендуется использовать более продвинутое логирование
            return null; // или выбросить пользовательское исключение
        }
    }
}
