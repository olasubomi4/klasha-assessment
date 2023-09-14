package com.klasha.assessment.model.response.countryInfo;

import lombok.Data;

@Data
public class CountryInfoResponse {
    private String country;
    private long population;
    private String capital;
    private CountryInfoLocation location;
    private String currency;
    private String iso2;
    private String iso3;
}
