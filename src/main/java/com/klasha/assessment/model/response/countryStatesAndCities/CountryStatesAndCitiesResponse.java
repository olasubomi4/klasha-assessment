package com.klasha.assessment.model.response.countryStatesAndCities;

import lombok.Data;

@Data
public class CountryStatesAndCitiesResponse {
    private boolean error;
    private String msg;
    private CountryStatesAndCitiesData data;
}
