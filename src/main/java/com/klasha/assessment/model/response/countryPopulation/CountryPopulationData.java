package com.klasha.assessment.model.response.countryPopulation;

import lombok.Data;

import java.util.List;

@Data
public class CountryPopulationData {
    private String country;
    private String code;
    private String iso3;
    private List<CountryPopulationDataPopulationCount> populationCounts;
}
