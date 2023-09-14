package com.klasha.assessment.model.response.countryLocation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CountryLocationData {
    private String name;
    private String iso2;
    @JsonProperty("long")
    private double longitude;
    @JsonProperty("lat")
    private double latitude;

}
