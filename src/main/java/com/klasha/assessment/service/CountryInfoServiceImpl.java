package com.klasha.assessment.service;

import com.klasha.assessment.entity.ExchangeRate;
import com.klasha.assessment.model.response.countryCapital.CountryCapitalResponse;
import com.klasha.assessment.model.response.countryCurrency.CountryCurrencyResponse;
import com.klasha.assessment.model.response.countryInfo.CountryInfoLocation;
import com.klasha.assessment.model.response.countryInfo.CountryInfoResponse;
import com.klasha.assessment.model.response.countryLocation.CountryLocationResponse;
import com.klasha.assessment.model.response.countryPopulation.CountryPopulationResponse;
import com.klasha.assessment.model.response.countryStateCities.CountryStateCitiesResponse;
import com.klasha.assessment.model.response.countryStates.CountryStatesDataState;
import com.klasha.assessment.model.response.countryStates.CountryStatesResponse;
import com.klasha.assessment.model.response.countryStatesAndCities.CountryStatesAndCitiesResponse;
import com.klasha.assessment.model.response.currencyConversion.CurrencyConversionResponse;
import com.klasha.assessment.repository.ExchangeRateRepository;

import com.klasha.assessment.utilities.CountryInfoServiceRestAsync;
import com.klasha.assessment.utilities.ErrorMessagesConstant;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CountryInfoServiceImpl implements CountryInfoService {
    private static Logger log = LogManager.getLogger(CityPopulationServiceImpl.class);

    private RestTemplate restTemplate;

    private ExchangeRateRepository exchangeRateRepository;

    private CountryInfoServiceRestAsync countryInfoServiceRestAsync;
    @Override
    public CountryInfoResponse getCountryInfo(String country) {
        try {
            CountryInfoResponse countryInfoResponse = new CountryInfoResponse();

            log.info("Opening a new thread to get the location of "+country);
            CompletableFuture<CountryLocationResponse> locationResponseCompletableFuture = countryInfoServiceRestAsync.getLocation(country)
                    .exceptionally(ex ->
                            {
                                throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                            }
                    );

            log.info("Opening a new thread to get the population of "+country);

            CompletableFuture<CountryPopulationResponse> populationResponseCompletableFuture = countryInfoServiceRestAsync.getPopulation(country)
                    .exceptionally(ex ->
                            {
                                throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                            }
                    );
            log.info("Opening a new thread to get the capital city of "+country);
            CompletableFuture<CountryCapitalResponse> capitalResponseCompletableFuture = countryInfoServiceRestAsync.getCapitalCity(country)
                    .exceptionally(ex ->
                            {
                                throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                            }
                    );

            log.info("Opening a new thread to get the currency of "+country);
            CompletableFuture<CountryCurrencyResponse> currencyResponseCompletableFuture = countryInfoServiceRestAsync.getCurrency(country)
                    .exceptionally(ex ->
                            {
                                throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                            }
                    );

            log.info("Waiting for all CompletableFuture instances to complete then join (block) until all CompletableFuture instances are completed");
            CompletableFuture.allOf(locationResponseCompletableFuture, populationResponseCompletableFuture, capitalResponseCompletableFuture, currencyResponseCompletableFuture).join();

            CountryPopulationResponse population=populationResponseCompletableFuture.get();
            countryInfoResponse.setCountry(population.getCountry());
            countryInfoResponse.setPopulation(population.getPopulation());

            CountryCapitalResponse  capitalResponse=capitalResponseCompletableFuture.get();
            countryInfoResponse.setCapital(capitalResponse.getCapital());

            CountryCurrencyResponse countryCurrencyResponse=currencyResponseCompletableFuture.get();
            countryInfoResponse.setIso3(countryCurrencyResponse.getIso3());
            countryInfoResponse.setIso2(countryCurrencyResponse.getIso2());
            countryInfoResponse.setCurrency(countryCurrencyResponse.getCurrency());

            CountryInfoLocation countryInfoLocation= new CountryInfoLocation();
            CountryLocationResponse countryLocationResponse =locationResponseCompletableFuture.get();
            countryInfoLocation.setLatitude(countryLocationResponse.getLatitude());
            countryInfoLocation.setLongitude(countryLocationResponse.getLongitude());
            countryInfoResponse.setLocation(countryInfoLocation);
            return countryInfoResponse;
        }
        catch (ExecutionException e) {
            log.error(e,e);
            throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
        } catch (InterruptedException e) {
            log.error(e,e);
            throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
        }
        catch (RuntimeException e) {
            log.error(e,e);
            throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
        }

    }

    @Override
    public CountryStatesAndCitiesResponse getStatesAndCitiesByCountry(String country) {
        try {
            CountryStatesAndCitiesResponse countryStatesAndCitiesResponse = new CountryStatesAndCitiesResponse();

            log.info("Opening a new thread to get the states in "+ country);
            CompletableFuture<CountryStatesResponse> countryStatesResponseCompletableFuture = countryInfoServiceRestAsync.getState(country);

            log.info("Waiting for CompletableFuture instance to complete then join (block) until CompletableFuture instance is completed");
            countryStatesResponseCompletableFuture.join();

            HashMap<String,CompletableFuture<CountryStateCitiesResponse>> statesAndCities= new HashMap<>();
            for (CountryStatesDataState state:
            countryStatesResponseCompletableFuture.get().getData().getStates()) {

                log.info("Opening a new thread to get cities in "+state);

                CompletableFuture<CountryStateCitiesResponse> countryStateCitiesResponseCompletableFuture =countryInfoServiceRestAsync.getCitiesInState(country,state.getName()).exceptionally(ex ->
                        {
                            throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                        }
                );
                statesAndCities.put(state.getName(),countryStateCitiesResponseCompletableFuture);
            }

            log.info("Collecting all CompletableFuture instances into a list");
            List<CompletableFuture<Void>> completableFutures = statesAndCities.values()
                    .stream()
                    .map(cf -> cf.thenAccept(result -> {}))
                    .collect(Collectors.toList());

            log.info("Waiting for all CompletableFuture instances to complete");
            CompletableFuture<Void> allOf = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));

            log.info("Joining (blocking) until all CompletableFuture instances are completed");
            allOf.join();


            Iterator<HashMap.Entry<String, CompletableFuture<CountryStateCitiesResponse>>> iterator = statesAndCities.entrySet().iterator();
            HashMap<String,List<String>> result = new HashMap<>();
            while (iterator.hasNext()) {
                Map.Entry<String, CompletableFuture<CountryStateCitiesResponse>> entry = iterator.next();
                String key = entry.getKey();
                CompletableFuture<CountryStateCitiesResponse> value = entry.getValue();
                result.put(key,value.get().getData());
            }
            countryStatesAndCitiesResponse.setCountry(country);
            countryStatesAndCitiesResponse.setStatesAndCities(result);
            return countryStatesAndCitiesResponse;
        } catch (ExecutionException e) {
            log.error(e,e);
            throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
        } catch (InterruptedException e) {
            log.error(e,e);
            throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
        }
        catch (RuntimeException e) {
            log.error(e,e);
            throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
        }

    }

    @Override
    public CurrencyConversionResponse convertCurrency(String country, BigDecimal amount, String targetCurrency) {
    try {
            CurrencyConversionResponse currencyConversionResponse= new CurrencyConversionResponse();

            log.info("Opening a new thread to get currency in "+country );
            CompletableFuture<CountryCurrencyResponse> countryCurrencyResponse= countryInfoServiceRestAsync.getCurrency(country)
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                    }
            );

            log.info("Waiting for CompletableFuture instance to complete then join (block) until CompletableFuture instance is completed");
            countryCurrencyResponse.join();

            String sourceCurrency =countryCurrencyResponse.get().getData().getCurrency();

            log.info("Getting exchange rates");
            Optional<ExchangeRate>exchangeRate = exchangeRateRepository.findExchangeRatesByTargetCurrencyAndSourceCurrency(targetCurrency,sourceCurrency);
            ExchangeRate unWrappedExchangeRate= unWrapExchangeRate(exchangeRate,targetCurrency,country);
            currencyConversionResponse.setCountryCurrency(sourceCurrency);
            currencyConversionResponse.setConvertedAmount(unWrappedExchangeRate.getRate().multiply(amount));
            currencyConversionResponse.setTargetCurrency(targetCurrency);
            return currencyConversionResponse;

        }
        catch (ExecutionException e) {
            log.error(e,e);
            throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
        } catch (InterruptedException e) {
            log.error(e,e);
            throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
        }
        catch (NotFoundException e) {
            log.error(e,e);
            throw new NotFoundException(e.getMessage());
        }
        catch (RuntimeException e) {
            log.error(e,e);
            throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
        }
    }

//    @Async
//    public CompletableFuture<CountryLocationResponse> getLocation(String country) {
//        try {
//            log.info("Getting location of " + country);
//            JSONObject requestBody = new JSONObject();
//            requestBody.put("country", country);
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.set("Content-Type", "application/json");
//            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);
//
//            log.info("Sending http post request, request body: "+requestBody.toString() );
//            ResponseEntity<CountryLocationResponse> responseEntity = restTemplate.exchange(
//                    "https://countriesnow.space/api/v0.1/countries/positions",
//                    HttpMethod.POST,
//                    httpEntity,
//                    CountryLocationResponse.class
//            );
//
//            log.info("Http response: "+responseEntity);
//
//            if (responseEntity.getStatusCode().is3xxRedirection()) {
//                HttpHeaders headers = responseEntity.getHeaders();
//                if (headers.containsKey("Location")) {
//                    String redirectUrl = headers.getFirst("Location");
//                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
//
//                    log.info("Redirecting http request to " +url);
//                    log.info("Sending http get request, request body: "+requestBody.toString() );
//
//                    // Make a new request to the redirect URL
//                    responseEntity = restTemplate.exchange(
//                            url,
//                            HttpMethod.GET,
//                            new HttpEntity<>(null, httpHeaders),
//                            CountryLocationResponse.class
//                    );
//                    log.info("Http response " +responseEntity);
//                }
//            }
//            return CompletableFuture.completedFuture(responseEntity.getBody());
//        } catch (Exception e) {
//            log.error("An exception occurred while trying to get the location of "+country+": "+e.toString());
//            throw new RuntimeException(e.getMessage(),e);
//        }
//    }
//
//    @Async
//    public CompletableFuture<CountryCapitalResponse> getCapitalCity(String country) {
//        try {
//            log.info("Getting capital city of  " + country);
//
//            JSONObject requestBody = new JSONObject();
//            requestBody.put("country", country);
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.set("Content-Type", "application/json");
//            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);
//
//            log.info("Sending http post request, request body: "+requestBody.toString() );
//
//            ResponseEntity<CountryCapitalResponse> responseEntity = restTemplate.exchange(
//                    "https://countriesnow.space/api/v0.1/countries/capital",
//                    HttpMethod.POST,
//                    httpEntity,
//                    CountryCapitalResponse.class
//            );
//
//            log.info("Http response: "+responseEntity);
//
//            // Check if the response status code is a redirection (3xx)
//            if (responseEntity.getStatusCode().is3xxRedirection()) {
//                HttpHeaders headers = responseEntity.getHeaders();
//                if (headers.containsKey("Location")) {
//                    String redirectUrl = headers.getFirst("Location");
//                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
//
//                    log.info("Redirecting http request to " +url);
//                    log.info("Sending http get request, request body: "+requestBody.toString() );
//
//                    // Make a new request to the redirect URL
//                    responseEntity = restTemplate.exchange(
//                            url,
//                            HttpMethod.GET,
//                            new HttpEntity<>(null, httpHeaders),
//                            CountryCapitalResponse.class
//                    );
//
//                    log.info("Http response " +responseEntity);
//                }
//            }
//            return CompletableFuture.completedFuture(responseEntity.getBody());
//        } catch (Exception e) {
//            log.error("An exception occurred while trying to get the capital of "+country+": "+e.toString());
//            throw new RuntimeException(e.getMessage(),e);
//        }
//    }
//
//    @Async
//    public CompletableFuture<CountryPopulationResponse> getPopulation(String country) {
//        try {
//            log.info("Getting population of " + country);
//            JSONObject requestBody = new JSONObject();
//            requestBody.put("country", country);
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.set("Content-Type", "application/json");
//            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);
//            log.info("Sending http post request, request body: "+requestBody.toString() );
//            ResponseEntity<CountryPopulationResponse> responseEntity = restTemplate.exchange(
//                    "https://countriesnow.space/api/v0.1/countries/population",
//                    HttpMethod.POST,
//                    httpEntity,
//                    CountryPopulationResponse.class
//            );
//
//            log.info("Http response: "+responseEntity);
//            // Check if the response status code is a redirection (3xx)
//            if (responseEntity.getStatusCode().is3xxRedirection()) {
//                HttpHeaders headers = responseEntity.getHeaders();
//                if (headers.containsKey("Location")) {
//                    String redirectUrl = headers.getFirst("Location");
//                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
//
//                    log.info("Redirecting http request to " +url);
//                    log.info("Sending http get request, request body: "+requestBody.toString() );
//
//                    // Make a new request to the redirect URL
//                    responseEntity = restTemplate.exchange(
//                            url,
//                            HttpMethod.GET,
//                            new HttpEntity<>(null, httpHeaders),
//                            CountryPopulationResponse.class
//                    );
//                    log.info("Http response " +responseEntity);
//                }
//            }
//            return CompletableFuture.completedFuture(responseEntity.getBody());
//        } catch (Exception e) {
//            log.error("An exception occurred while trying to get the population of "+country+": "+e.toString());
//            throw new RuntimeException(e.getMessage(),e);
//        }
//    }
//
//
//    public CompletableFuture<CountryCurrencyResponse> getCurrency(String country) {
//        try {
//            log.info("Getting currency of " + country);
//            JSONObject requestBody = new JSONObject();
//            requestBody.put("country", country);
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.set("Content-Type", "application/json");
//            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);
//
//            log.info("Sending http post request, request body: "+requestBody.toString() );
//            ResponseEntity<CountryCurrencyResponse> responseEntity = restTemplate.exchange(
//                    "https://countriesnow.space/api/v0.1/countries/currency",
//                    HttpMethod.POST,
//                    httpEntity,
//                    CountryCurrencyResponse.class
//            );
//
//            log.info("Http response: "+responseEntity);
//
//            // Check if the response status code is a redirection (3xx)
//            if (responseEntity.getStatusCode().is3xxRedirection()) {
//                HttpHeaders headers = responseEntity.getHeaders();
//                if (headers.containsKey("Location")) {
//                    String redirectUrl = headers.getFirst("Location");
//                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
//
//                    log.info("Redirecting http request to " +url);
//                    log.info("Sending http get request, request body: "+requestBody.toString() );
//
//                    // Make a new request to the redirect URL
//                    responseEntity = restTemplate.exchange(
//                            url,
//                            HttpMethod.GET,
//                            new HttpEntity<>(null, httpHeaders),
//                            CountryCurrencyResponse.class
//                    );
//                    log.info("Http response " +responseEntity);
//                }
//            }
//
//            return CompletableFuture.completedFuture(responseEntity.getBody());
//        } catch (Exception e) {
//            log.error("An exception occurred while trying to get the currency of "+country+": "+e.toString());
//            throw new RuntimeException(e.getMessage(),e);
//        }
//    }
//
//    @Async
//    public CompletableFuture<CountryStatesResponse> getState(String country) {
//        try {
//            log.info("Getting states in " + country);
//            JSONObject requestBody = new JSONObject();
//            requestBody.put("country", country);
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.set("Content-Type", "application/json");
//            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);
//            log.info("Sending http post request, request body: "+requestBody.toString() );
//            ResponseEntity<CountryStatesResponse> responseEntity = restTemplate.exchange(
//                    "https://countriesnow.space/api/v0.1/countries/states",
//                    HttpMethod.POST,
//                    httpEntity,
//                    CountryStatesResponse.class
//            );
//
//            log.info("Http response: "+responseEntity);
//            // Check if the response status code is a redirection (3xx)
//            if (responseEntity.getStatusCode().is3xxRedirection()) {
//                HttpHeaders headers = responseEntity.getHeaders();
//                if (headers.containsKey("Location")) {
//                    String redirectUrl = headers.getFirst("Location");
//                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
//                    log.info("Redirecting http request to " +url);
//                    log.info("Sending http get request, request body: "+requestBody.toString() );
//
//                    // Make a new request to the redirect URL
//                    responseEntity = restTemplate.exchange(
//                            url,
//                            HttpMethod.GET,
//                            new HttpEntity<>(null, httpHeaders),
//                            CountryStatesResponse.class
//                    );
//                    log.info("Http response " +responseEntity);
//                }
//            }
//            return CompletableFuture.completedFuture(responseEntity.getBody());
//        } catch (Exception e) {
//            log.error("An exception occurred while trying to get the states in "+country+": "+e.toString());
//            throw new RuntimeException(e.getMessage(),e);
//        }
//    }
//
//    @Async
//    public CompletableFuture<CountryStateCitiesResponse> getCitiesInState(String country, String state) {
//        try {
//            log.info("Getting states in " + country);
//            JSONObject requestBody = new JSONObject();
//            requestBody.put("country", country);
//            requestBody.put("state", state);
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.set("Content-Type", "application/json");
//            HttpEntity httpEntity = new HttpEntity(requestBody.toString(), httpHeaders);
//            log.info("Sending http post request, request body: "+requestBody.toString() );
//
//            ResponseEntity<CountryStateCitiesResponse> responseEntity = restTemplate.exchange(
//                    "https://countriesnow.space/api/v0.1/countries/state/cities",
//                    HttpMethod.POST,
//                    httpEntity,
//                    CountryStateCitiesResponse.class
//            );
//
//            log.info("Http response: "+responseEntity);
//
//            // Check if the response status code is a redirection (3xx)
//            if (responseEntity.getStatusCode().is3xxRedirection()) {
//                HttpHeaders headers = responseEntity.getHeaders();
//                if (headers.containsKey("Location")) {
//                    String redirectUrl = headers.getFirst("Location");
//                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
//                    ;
//                    log.info("Redirecting http request to " +url);
//                    log.info("Sending http get request, request body: "+requestBody.toString() );
//
//                    // Make a new request to the redirect URL
//                    responseEntity = restTemplate.exchange(
//                            url,
//                            HttpMethod.GET,
//                            new HttpEntity<>(null, httpHeaders),
//                            CountryStateCitiesResponse.class
//                    );
//                    log.info("Http response " +responseEntity);
//                }
//            }
//            return CompletableFuture.completedFuture(responseEntity.getBody());
//        } catch (Exception e) {
//            log.error("An exception occurred while trying to get the cities in "+state+": "+e.toString());
//            throw new RuntimeException(e.getMessage(),e);
//        }
//    }

    static ExchangeRate unWrapExchangeRate(Optional<ExchangeRate> entity, String targetCurrency,String country) {
        if (entity.isPresent()) {
            return entity.get();
        }
        else {
            log.info("Currency conversion not available for the " + country + " and " + targetCurrency + " Please check the provided country and currency codes, and ensure they are valid for conversion");
            throw new NotFoundException("Currency conversion not available for the " + country + " and " + targetCurrency + " Please check the provided country and currency codes, and ensure they are valid for conversion");
        }
    }

}
