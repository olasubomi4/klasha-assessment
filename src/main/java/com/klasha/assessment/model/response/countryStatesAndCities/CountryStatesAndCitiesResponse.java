package com.klasha.assessment.model.response.countryStatesAndCities;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CountryStatesAndCitiesResponse {

    private String country;
    private Map<String, List<String>> statesAndCities;
}
