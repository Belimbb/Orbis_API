package main.requests;

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
                "NAME","BVDID","Match.EmailOrWebsite","NATIONAL_ID","NATIONAL_ID_LABEL","COUNTRY",
                "Match.Name_Local","CPYCONTACTS_HEADER_FullNameOriginalLanguagePreferred","CPYCONTACTS_HEADER_IdDirector",
                "CPYCONTACTS_MEMBERSHIP_Function","CPYCONTACTS_MEMBERSHIP_CurrentPrevious","CPYCONTACTS_HEADER_Birthdate",
                "CPYCONTACTS_HEADER_MultipleNationalitiesLabel","CPYCONTACTS_MEMBERSHIP_IsAShareholderFormatted","BO_NAME",
                "BO_BVD_ID_NUMBER","BO_COUNTRY_ISO_CODE","BO_WORLDCOMPLIANCE_MATCH_EXCEPT_SBE_INDICATOR","SH_NAME","SH_BVD_ID_NUMBER",
                "SH_COUNTRY_ISO_CODE","SH_DIRECT_PCT","SH_NATIONAL_ID","SH_WEBSITE","GUO_NAME","SUB_NAME","SUB_BVD_ID_NUMBER",
                "SUB_COUNTRY_ISO_CODE","SUB_DIRECT_PCT","SUB_NATIONAL_ID","SUB_WEBSITE","BRANCH_NAME","BRANCH_BVD_ID_NUMBER",
                "BRANCH_COUNTRY_ISO_CODE","NEW_COMPANY_DATE"
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
