package main;

import main.requests.MultiRequest;
import main.service.User;

import java.util.HashMap;
import java.util.Map;

// Company's (Owners)= https://api.bvdinfo.com/v1/orbis/Companies/data?query=
//Contacts (Directors) = https://api.bvdinfo.com/v1/orbis/contacts/data?query=
public class AppLauncher {
    public void run(){
        String token = "2LK951a1674f439eee11abd50278abee30dc";
        // Создаем экземпляры классов
        User user = new User(token);

        MultiRequest multiRequest = new MultiRequest();

        // Создаем запрос
        Map<String, String> searchParameters = new HashMap<>();
        searchParameters.put("Name", "ALFA-BANK");
        searchParameters.put("OrbisID", "033219808");

        // Создаем и отправляем запрос
        String query = multiRequest.createQuery(searchParameters, "ExcludeBranchLocations");

        // Отправляем запрос и получаем ответ
        String jsonResponse = user.getResponseParser().sendRequest("companies", query);

        // Например, распечатать ответ или преобразовать его в определенный объект
        System.out.println(jsonResponse);
    }
}

