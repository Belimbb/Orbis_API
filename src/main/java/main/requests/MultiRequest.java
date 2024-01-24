package main.requests;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class MultiRequest {

    private static final String BASE_URL = "https://api.bvdinfo.com/v1/Orbis/Companies/data?Query=";

    // Конструктор и любые необходимые поля

    // Метод для создания URL запроса
    public static String getMultiRequestUrl() {
        return BASE_URL;
    }

    // Метод для создания JSON запроса
    public static String createMultiRequestQuery(Map<String, String> criteria) {
        JsonObject matchObject = getJsonObject(criteria);

        JsonObject whereCondition = new JsonObject();
        whereCondition.add("MATCH", matchObject);

        JsonArray whereArray = new JsonArray();
        whereArray.add(whereCondition);

        // Добавьте все поля, которые необходимо выбрать
        String[] selectFieldsArray = new String[]{
                "NAME","BVDID","Match.EmailOrWebsite","NATIONAL_ID","NATIONAL_ID_LABEL","COUNTRY","OVERVIEW_FULL_OVERVIEW","Match.Name_Local"
        };

        JsonArray selectFields = new JsonArray();
        for (String field : selectFieldsArray) {
            selectFields.add(field);
        }

        JsonObject queryObject = new JsonObject();
        queryObject.add("WHERE", whereArray);
        queryObject.add("SELECT", selectFields);

        return queryObject.toString();
    }

    @NotNull
    private static JsonObject getJsonObject(Map<String, String> criteria) {
        JsonObject matchCriteria = new JsonObject();
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                matchCriteria.addProperty(entry.getKey(), entry.getValue());
            }
        }

        JsonObject matchObject = new JsonObject();
        matchObject.add("Criteria", matchCriteria);

        JsonObject optionsObject = new JsonObject();
        optionsObject.addProperty("SelectionMode", "normal");
        JsonArray exclusionFlags = new JsonArray();
        exclusionFlags.add("ExcludeBranchLocations");
        optionsObject.add("ExclusionFlags", exclusionFlags);

        matchObject.add("Options", optionsObject);
        return matchObject;
    }
}
