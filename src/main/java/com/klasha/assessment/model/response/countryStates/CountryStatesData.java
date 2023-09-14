package com.klasha.assessment.model.response.countryStates;

import lombok.Data;

import java.util.List;

@Data
public class CountryStatesData {
    private String name;
    private String iso3;
    private List<CountryStatesDataState> states;
}
