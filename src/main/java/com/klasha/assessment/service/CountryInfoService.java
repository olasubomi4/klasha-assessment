package com.klasha.assessment.service;

import com.klasha.assessment.model.response.countryInfo.CountryInfoResponse;
import com.klasha.assessment.model.response.countryStatesAndCities.CountryStatesAndCitiesResponse;
import com.klasha.assessment.model.response.currencyConversion.CurrencyConversionResponse;

import java.math.BigDecimal;

public interface CountryInfoService {

    CountryInfoResponse getCountryInfo(String country);
    CountryStatesAndCitiesResponse getStatesAndCitiesByCountry (String country);
    CurrencyConversionResponse convertCurrency (String country, BigDecimal amount, String targetCurrency);

}
