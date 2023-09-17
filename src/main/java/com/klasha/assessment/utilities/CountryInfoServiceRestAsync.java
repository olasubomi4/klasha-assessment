package com.klasha.assessment.utilities;

import com.klasha.assessment.model.response.countryCapital.CountryCapitalResponse;
import com.klasha.assessment.model.response.countryCurrency.CountryCurrencyResponse;
import com.klasha.assessment.model.response.countryLocation.CountryLocationResponse;
import com.klasha.assessment.model.response.countryPopulation.CountryPopulationResponse;
import com.klasha.assessment.model.response.countryStateCities.CountryStateCitiesResponse;
import com.klasha.assessment.model.response.countryStates.CountryStatesResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class CountryInfoServiceRestAsync {

    final RestTemplate restTemplate;


    private static Logger log = LogManager.getLogger(CountryInfoServiceRestAsync.class);

    @Async
    public CompletableFuture<CountryLocationResponse> getLocation(String country) {
        try {
            log.info("Getting location of " + country);
            JSONObject requestBody = new JSONObject();
            requestBody.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);

            log.info("Sending http post request, request body: "+requestBody.toString() );
            ResponseEntity<CountryLocationResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/positions",
                    HttpMethod.POST,
                    httpEntity,
                    CountryLocationResponse.class
            );

            log.info("Http response: "+responseEntity);

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
                            CountryLocationResponse.class
                    );
                    log.info("Http response " +responseEntity);
                }
            }
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.error("An exception occurred while trying to get the location of "+country+": "+e.toString());
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Async
    public CompletableFuture<CountryCapitalResponse> getCapitalCity(String country) {
        try {
            log.info("Getting capital city of  " + country);

            JSONObject requestBody = new JSONObject();
            requestBody.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);

            log.info("Sending http post request, request body: "+requestBody.toString() );

            ResponseEntity<CountryCapitalResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/capital",
                    HttpMethod.POST,
                    httpEntity,
                    CountryCapitalResponse.class
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
                            CountryCapitalResponse.class
                    );

                    log.info("Http response " +responseEntity);
                }
            }
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.error("An exception occurred while trying to get the capital of "+country+": "+e.toString());
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Async
    public CompletableFuture<CountryPopulationResponse> getPopulation(String country) {
        try {
            log.info("Getting population of " + country);
            JSONObject requestBody = new JSONObject();
            requestBody.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);
            log.info("Sending http post request, request body: "+requestBody.toString() );
            ResponseEntity<CountryPopulationResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/population",
                    HttpMethod.POST,
                    httpEntity,
                    CountryPopulationResponse.class
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
                            CountryPopulationResponse.class
                    );
                    log.info("Http response " +responseEntity);
                }
            }
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.error("An exception occurred while trying to get the population of "+country+": "+e.toString());
            throw new RuntimeException(e.getMessage(),e);
        }
    }


    public CompletableFuture<CountryCurrencyResponse> getCurrency(String country) {
        try {
            log.info("Getting currency of " + country);
            JSONObject requestBody = new JSONObject();
            requestBody.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);

            log.info("Sending http post request, request body: "+requestBody.toString() );
            ResponseEntity<CountryCurrencyResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/currency",
                    HttpMethod.POST,
                    httpEntity,
                    CountryCurrencyResponse.class
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
                            CountryCurrencyResponse.class
                    );
                    log.info("Http response " +responseEntity);
                }
            }

            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.error("An exception occurred while trying to get the currency of "+country+": "+e.toString());
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Async
    public CompletableFuture<CountryStatesResponse> getState(String country) {
        try {
            log.info("Getting states in " + country);
            JSONObject requestBody = new JSONObject();
            requestBody.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);
            log.info("Sending http post request, request body: "+requestBody.toString() );
            ResponseEntity<CountryStatesResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/states",
                    HttpMethod.POST,
                    httpEntity,
                    CountryStatesResponse.class
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
                            CountryStatesResponse.class
                    );
                    log.info("Http response " +responseEntity);
                }
            }
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.error("An exception occurred while trying to get the states in "+country+": "+e.toString());
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Async
    public CompletableFuture<CountryStateCitiesResponse> getCitiesInState(String country, String state) {
        try {
            log.info("Getting states in " + country);
            JSONObject requestBody = new JSONObject();
            requestBody.put("country", country);
            requestBody.put("state", state);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);
            log.info("Sending http post request, request body: "+requestBody.toString() );

            ResponseEntity<CountryStateCitiesResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/state/cities",
                    HttpMethod.POST,
                    httpEntity,
                    CountryStateCitiesResponse.class
            );

            log.info("Http response: "+responseEntity);

            // Check if the response status code is a redirection (3xx)
            if (responseEntity.getStatusCode().is3xxRedirection()) {
                HttpHeaders headers = responseEntity.getHeaders();
                if (headers.containsKey("Location")) {
                    String redirectUrl = headers.getFirst("Location");
                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
                    ;
                    log.info("Redirecting http request to " +url);
                    log.info("Sending http get request, request body: "+requestBody.toString() );

                    // Make a new request to the redirect URL
                    responseEntity = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            new HttpEntity<>(null, httpHeaders),
                            CountryStateCitiesResponse.class
                    );
                    log.info("Http response " +responseEntity);
                }
            }
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.error("An exception occurred while trying to get the cities in "+state+": "+e.toString());
            throw new RuntimeException(e.getMessage(),e);
        }
    }

}
