package com.klasha.assessment.model.response.filterCitiesAndPopulation;

import lombok.Data;

@Data
public class FilterCitiesAndPopulationCityInfoPopulationCount {
    private String year;
    private Long value;
    private String sex;
    private String reliability;
}
