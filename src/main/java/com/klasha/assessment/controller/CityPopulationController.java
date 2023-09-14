package com.klasha.assessment.controller;

import com.klasha.assessment.model.response.cityPopulation.CityPopulationResponse;
import com.klasha.assessment.service.CityPopulationService;
import com.klasha.assessment.validators.IntegerNotBlank;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "City Population Controller")
@RestController
@AllArgsConstructor
@RequestMapping("/api/${apiVersion}")
public class CityPopulationController {

    CityPopulationService cityPopulationService;

    @Operation(description = "Retrieves most populated cities in  Italy, New Zealand and Ghana ordered by population descending")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CityPopulationResponse.class))))
    @GetMapping(value = "/most-populated-cities", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CityPopulationResponse>> getMostPopulatedCities(@RequestParam("numberOfCities") Integer numberOfCities) {
        List<CityPopulationResponse> cityPopulationResponse= cityPopulationService.getMostPopulatedCities(numberOfCities);
        return new ResponseEntity<>(cityPopulationResponse, HttpStatus.OK);
    }
}
