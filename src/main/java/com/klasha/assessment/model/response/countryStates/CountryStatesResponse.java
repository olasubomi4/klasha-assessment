package com.klasha.assessment.model.response.countryStates;

import lombok.Data;

@Data
public class CountryStatesResponse {
    private boolean error;
    private String msg;
    private CountryStatesData data;

}
