package com.klasha.assessment.utilities;

import com.klasha.assessment.model.response.filterCitiesAndPopulation.FilterCitiesAndPopulationResponse;
import com.klasha.assessment.service.CityPopulationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class CityPopulationServiceRestAsync {

    @Autowired
    final RestTemplate restTemplate;
    private static Logger log = LogManager.getLogger(CityPopulationServiceRestAsync.class);


    @Async("taskExecutor")
    public CompletableFuture<FilterCitiesAndPopulationResponse> getMostPopulatedCities(Integer numberOfCities, String country) throws InterruptedException, UnsupportedEncodingException {
        try {
            log.info("Getting most in populated " + country);

            JSONObject requestBody = new JSONObject();
            requestBody.put("limit", numberOfCities);
            requestBody.put("order", "dsc");
            requestBody.put("orderBy", "population");
            requestBody.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");

            log.info("Sending http post request, request body: "+requestBody.toString() );

            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);
            ResponseEntity<FilterCitiesAndPopulationResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/population/cities/filter",
                    HttpMethod.POST,
                    httpEntity,
                    FilterCitiesAndPopulationResponse.class
            );

            log.info("Http response: "+responseEntity);

            // Check if the response status code is a redirection (3xx)
            if (responseEntity.getStatusCode().is3xxRedirection()) {
                HttpHeaders headers = responseEntity.getHeaders();
                if (headers.containsKey("Location")) {
                    String redirectUrl = headers.getFirst("Location");
                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");

                    log.info("Redirecting http request to " +url);
                    log.info("Sending http get request, request body: "+requestBody.toString() );

                    // Make a new request to the redirect URL
                    responseEntity = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            new HttpEntity<>(null, httpHeaders),
                            FilterCitiesAndPopulationResponse.class
                    );

                    log.info("Http response " +responseEntity);
                }
            }
            return CompletableFuture.completedFuture(responseEntity.getBody());
        }
        catch (Exception e)
        {
            log.error("An exception occurred for " + country + ": "+e.toString());
            throw new RuntimeException(e.getMessage(),e);
        }
    }
}
