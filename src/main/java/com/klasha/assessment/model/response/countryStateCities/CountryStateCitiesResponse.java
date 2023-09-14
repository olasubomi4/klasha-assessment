package com.klasha.assessment.model.response.countryStateCities;

import lombok.Data;

import java.util.List;

@Data
public class CountryStateCitiesResponse {
    private boolean error;
    private String msg;
    private List<String> data;
}
