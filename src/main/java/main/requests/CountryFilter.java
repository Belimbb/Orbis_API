package main.requests;

import lombok.Getter;

@Getter
public enum CountryFilter {
    VAT("100131"),
    TIN("100263"),
    LEI("100115");

    private final String code;
    CountryFilter(String code){
        this.code = code;
    }
}
