package com.klasha.assessment.service;

import com.klasha.assessment.model.response.cityPopulation.CityPopulationResponse;

import java.util.List;

public interface CityPopulationService {
     List<CityPopulationResponse> getMostPopulatedCities(Integer numberOfCities);
}
