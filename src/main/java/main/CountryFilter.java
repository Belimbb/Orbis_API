package main;

public enum CountryFilter {
    VAT("100131"),
    TIN("100263"),
    LEI("100115");

    private String code;
    CountryFilter(String code){
        this.code = code;
    }
    public String getCode(){
        return code;
    }
}
