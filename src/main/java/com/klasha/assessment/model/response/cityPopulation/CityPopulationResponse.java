package com.klasha.assessment.model.response.cityPopulation;

import lombok.Data;
import org.json.JSONObject;

import java.util.List;

@Data
public class CityPopulationResponse {

    private String country;
    private List<CityPopulationCity> cities;

    @Override
    public String toString() {
      return new JSONObject(this).toString();
    }
}
