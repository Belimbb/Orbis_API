package main.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
public class MultiRequest {

    public String createQuery(Map<String, String> searchParameters, String exclusionFlag) {
        JsonObject whereCondition = getJsonObject(searchParameters, exclusionFlag);

        JsonArray whereArray = new JsonArray();
        whereArray.add(whereCondition);

        JsonArray selectArray = new JsonArray();
        selectArray.add("NAME");
        selectArray.add("BVDID");
        selectArray.add("Match.EmailOrWebsite");
        selectArray.add("NATIONAL_ID");
        selectArray.add("AKA_NAME");
        selectArray.add("BO_NAME");

        JsonObject fullRequest = new JsonObject();
        fullRequest.add("WHERE", whereArray);
        fullRequest.add("SELECT", selectArray);

        return fullRequest.toString();
    }

    @NotNull
    private static JsonObject getJsonObject(Map<String, String> searchParameters, String exclusionFlag) {
        JsonObject matchCriteria = new JsonObject();
        for (Map.Entry<String, String> entry : searchParameters.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                matchCriteria.addProperty(entry.getKey(), entry.getValue());
            }
        }

        JsonObject matchObject = new JsonObject();
        matchObject.add("Criteria", matchCriteria);

        JsonObject optionsObject = new JsonObject();
        optionsObject.addProperty("SelectionMode", "normal");
        JsonArray exclusionFlags = new JsonArray();
        exclusionFlags.add(exclusionFlag);
        optionsObject.add("ExclusionFlags", exclusionFlags);
        matchObject.add("Options", optionsObject);

        JsonObject whereCondition = new JsonObject();
        whereCondition.add("MATCH", matchObject);
        return whereCondition;
    }
}
