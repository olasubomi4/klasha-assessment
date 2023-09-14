package com.klasha.assessment.model.response.filterCitiesAndPopulation;

import lombok.Data;

import java.util.List;

@Data
public class FilterCitiesAndPopulationCityInfo {
    private String city;
    private String country;
    private List<FilterCitiesAndPopulationCityInfoPopulationCount> populationCounts;
}
