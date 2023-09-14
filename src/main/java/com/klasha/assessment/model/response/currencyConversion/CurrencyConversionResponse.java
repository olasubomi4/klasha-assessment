package com.klasha.assessment.model.response.currencyConversion;

import lombok.Data;
import org.json.JSONObject;

@Data
public class CurrencyConversionResponse {
    private String countryCurrency;
    private double convertedAmount;
    private String targetCurrency;

    @Override
    public String toString() {
     return new JSONObject(this).toString();
    }
}