package main.requests;

import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder {

    public String buildDirectorsQuery(List<String> TINs, CountryFilter countryFilter) {
        String jsonQuery = "{\"WHERE\":[{\"FromCompanies\":{\"Identifiers\":{\"Ids\":[%s],\"CountryFilter\":[\"%s\"]}}},{\"Type\":[\"Individual\"]}],\"SELECT\":[\"CONTACTS_HEADER_BareTitle\",\"CONTACTS_HEADER_Birthdate\",\"CONTACTS_HEADER_FirstName\",\"CONTACTS_HEADER_MiddleName\",\"CONTACTS_HEADER_LastName\",\"CONTACTS_HEADER_FullName\",\"CONTACTS_HEADER_IdDirector\",\"CONTACTS_HEADER_NationalityCountryLabel\",{\"MEMBERSHIP_DATA\":{\"FILTERS\":\"Filter.Name=ContactsFilter;ContactsFilter.CurrentPreviousQueryString=0;ContactsFilter.IfHomeOnlyReturnCountry=1;ContactsFilter.Currents=True;ContactsFilter.SourcesToExcludeQueryString=59B|69B|70B|99B\",\"SELECT\":[\"CONTACTS_MEMBERSHIP_IdCompany\",\"CONTACTS_MEMBERSHIP_NameCompany\",\"CONTACTS_MEMBERSHIP_BeginningNominationDate\",\"CONTACTS_MEMBERSHIP_CurrentOrPreviousStr\",\"CONTACTS_MEMBERSHIP_EndExpirationDate\",\"CONTACTS_MEMBERSHIP_Function\"]}}]}";
        String tins = convertArrayToString(TINs);
        return String.format(jsonQuery, tins, countryFilter.getCode());
    }

    public String buildOwnersQuery(List<String> TINs, CountryFilter countryFilter) {
        String jsonQuery = "{\"WHERE\":[{\"Identifiers\":{\"Ids\":[%s],\"CountryFilter\":[\"%s\"]}}],\"SELECT\":[\"NAME\",\"BVD9\",\"BVD_ID_NUMBER\",{\"BENFICIAL_OWNERS\":{\"SELECT\":[\"BO_BIRTHDATE\",\"BO_BVD_ID_NUMBER\",\"BO_COUNTRY_ISO_CODE\",\"BO_ENTITY_TYPE\",\"BO_FIRST_NAME\",\"BO_LAST_NAME\",\"BO_NAME\",\"BO_UCI\"]}},{\"SHAREHOLDERS\":{\"SELECT\":[\"SH_BVD_ID_NUMBER\",\"SH_BVD9\",\"SH_COUNTRY_ISO_CODE\",\"SH_DIRECT_PCT\",\"SH_ENTITY_TYPE\",\"SH_LAST_NAME\",\"SH_LEI\",\"SH_NAME\",\"SH_STATE_PROVINCE\",\"SH_UCI\",\"SH_FIRST_NAME\"]}}]}";
        String tins = convertArrayToString(TINs);
        return String.format(jsonQuery, tins, countryFilter.getCode());
    }

    //don't use
    public String buildBvDIDsQuery(String code, CountryFilter cf) {
        String jsonQuery = "{\"WHERE\":[{\"Identifiers\":{\"Ids\":[\"%s\"],\"CountryFilter\":[\"%s\"]}}],\"SELECT\":[\"BVDID\"]}";
        return String.format(jsonQuery, code, cf.getCode());
    }

    private String convertArrayToString(List<String> Arr) {
        return Arr.stream()
                .map(bv -> "\"" + bv + "\"")
                .collect(Collectors.joining(","));
    }
}

