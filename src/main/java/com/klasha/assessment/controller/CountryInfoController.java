package com.klasha.assessment.controller;

import com.klasha.assessment.model.response.countryInfo.CountryInfoResponse;
import com.klasha.assessment.model.response.countryStatesAndCities.CountryStatesAndCitiesResponse;
import com.klasha.assessment.model.response.currencyConversion.CurrencyConversionResponse;
import com.klasha.assessment.service.CountryInfoService;
import com.klasha.assessment.utilities.PatternFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@Tag(name = "Country Info Controller", description = "Retrieve information about a country")
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v1/country-info")
public class CountryInfoController {

    CountryInfoService countryInfoService;

    @Operation(summary = "This endpoint takes a country name as a parameter and provides detailed information about the specified country, including its population, capital city, geographical location (latitude and longitude), currency, and ISO2&3 country codes.")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryInfoResponse.class))))
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CountryInfoResponse> getCountryInfo(@RequestParam("country") @NotBlank(message = "country is required") @Pattern(regexp = PatternFormat.ALPHABETS,message = "Country should not String contains special characters or digits") String country) {
        CountryInfoResponse countryInfoResponse= countryInfoService.getCountryInfo(country);
        return new ResponseEntity<>(countryInfoResponse, HttpStatus.OK);
    }

    @Operation(summary = "This endpoint allows users to retrieve a comprehensive list of all states and cities within a specified country. Users provide the country name as a parameter, and the endpoint returns a detailed list of all states in the country, along with the cities within each state")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryStatesAndCitiesResponse.class))))
    @GetMapping(value = "/details/states-and-cities", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CountryStatesAndCitiesResponse> getStatesAndCitiesByCountry(@RequestParam(name="country")@Pattern(regexp = PatternFormat.ALPHABETS,message = "Country should not String contains special characters or digits") @NotBlank(message = "country is required") String country) {
        CountryStatesAndCitiesResponse countryStatesAndCitiesResponse= countryInfoService.getStatesAndCitiesByCountry(country);
        return new ResponseEntity<>(countryStatesAndCitiesResponse, HttpStatus.OK);
    }

    @Operation(summary = "This endpoint allows users to convert a monetary amount from one currency to another based on a given country and target currency. It provides information about the country's currency, converts the amount, and formats it correctly in the target currency.")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CurrencyConversionResponse.class))))
    @GetMapping(value = "/currency-converter/convert", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<
            CurrencyConversionResponse> convertCurrency(@RequestParam(name="country") @NotBlank(message = "country is required") @Pattern(regexp = PatternFormat.ALPHABETS,message = "Country should not String contains special characters or digits") String country,
                                                        @RequestParam(name="amount") @Min(message = "amount must be greater than 0",value = 1) BigDecimal amount,
                                                        @RequestParam(name="targetCurrency") @NotBlank(message = "targetCurrency is required")  @Pattern(regexp = PatternFormat.ALPHABETS,message = "target currency should not String contains special characters or digits") String targetCurrency) {

        CurrencyConversionResponse currencyConversionResponse= countryInfoService.convertCurrency(country,amount,targetCurrency);
        return new ResponseEntity<>(currencyConversionResponse, HttpStatus.OK);
    }


}
