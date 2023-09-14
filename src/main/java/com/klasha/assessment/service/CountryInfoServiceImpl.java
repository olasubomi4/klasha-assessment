package com.klasha.assessment.service;

import com.klasha.assessment.entity.ExchangeRate;
import com.klasha.assessment.exception.CustomRuntimeException;
import com.klasha.assessment.model.response.countryCapital.CountryCapitalResponse;
import com.klasha.assessment.model.response.countryCurrency.CountryCurrencyResponse;
import com.klasha.assessment.model.response.countryInfo.CountryInfoLocation;
import com.klasha.assessment.model.response.countryInfo.CountryInfoResponse;
import com.klasha.assessment.model.response.countryLocation.CountryLocationResponse;
import com.klasha.assessment.model.response.countryPopulation.CountryPopulationResponse;
import com.klasha.assessment.model.response.countryStateCities.CountryStateCitiesResponse;
import com.klasha.assessment.model.response.countryStates.CountryStatesDataState;
import com.klasha.assessment.model.response.countryStates.CountryStatesResponse;
import com.klasha.assessment.model.response.countryStatesAndCities.CountryStatesAndCitiesData;
import com.klasha.assessment.model.response.countryStatesAndCities.CountryStatesAndCitiesResponse;
import com.klasha.assessment.model.response.currencyConversion.CurrencyConversionResponse;
import com.klasha.assessment.repository.ExchangeRateRepository;

import com.klasha.assessment.utilities.SslManager;
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

import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CountryInfoServiceImpl implements CountryInfoService {
    private static Logger log = LogManager.getLogger(CityPopulationServiceImpl.class);
    SslManager sslManger;

    private RestTemplate restTemplate;

    private ExchangeRateRepository exchangeRateRepository;
    @Override
    public CountryInfoResponse getCountryInfo(String country) {
        try {
            CountryInfoResponse countryInfoResponse = new CountryInfoResponse();

            CompletableFuture<CountryLocationResponse> locationResponseCompletableFuture = getLocation(country);
            CompletableFuture<CountryPopulationResponse> populationResponseCompletableFuture = getPopulation(country);
            CompletableFuture<CountryCapitalResponse> capitalResponseCompletableFuture = getCapitalCity(country);
            CompletableFuture<CountryCurrencyResponse> currencyResponseCompletableFuture = getCurrency(country);

            CompletableFuture.allOf(locationResponseCompletableFuture, populationResponseCompletableFuture, capitalResponseCompletableFuture, currencyResponseCompletableFuture).join();

            CountryPopulationResponse population=populationResponseCompletableFuture.get();
            countryInfoResponse.setCountry(population.getCountry());
            countryInfoResponse.setPopulation(population.getPopulation());

            CountryCapitalResponse  capitalResponse=capitalResponseCompletableFuture.get();
            countryInfoResponse.setCapital(capitalResponse.getCapital());

            CountryCurrencyResponse countryCurrencyResponse  =currencyResponseCompletableFuture.get();
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
        catch (Exception e)
        {
            throw new CustomRuntimeException(e.getMessage(),e);        }
    }

    @Override
    public CountryStatesAndCitiesResponse getStatesAndCitiesByCountry(String country) {
        try {
            CountryStatesAndCitiesResponse countryStatesAndCitiesResponse = new CountryStatesAndCitiesResponse();
            CompletableFuture<CountryStatesResponse> countryStatesResponseCompletableFuture = getState(country);
            countryStatesResponseCompletableFuture.join();

            HashMap<String,CompletableFuture<CountryStateCitiesResponse>> statesAndCities= new HashMap<>();
            for (CountryStatesDataState state:
            countryStatesResponseCompletableFuture.get().getData().getStates()) {
                CompletableFuture<CountryStateCitiesResponse> countryStateCitiesResponseCompletableFuture = getCitiesInState(country,state.getName());
                statesAndCities.put(state.getName(),countryStateCitiesResponseCompletableFuture);
            }

            // Collect all CompletableFuture instances into a list
            List<CompletableFuture<Void>> completableFutures = statesAndCities.values()
                    .stream()
                    .map(cf -> cf.thenAccept(result -> {}))
                    .collect(Collectors.toList());

            // Wait for all CompletableFuture instances to complete
            CompletableFuture<Void> allOf = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));

            // Join (block) until all CompletableFuture instances are completed
            allOf.join();


            Iterator<HashMap.Entry<String, CompletableFuture<CountryStateCitiesResponse>>> iterator = statesAndCities.entrySet().iterator();
            HashMap<String,List<String>> result = new HashMap<>();
            CountryStatesAndCitiesData countryStatesAndCitiesData=new CountryStatesAndCitiesData();
            while (iterator.hasNext()) {
                Map.Entry<String, CompletableFuture<CountryStateCitiesResponse>> entry = iterator.next();
                String key = entry.getKey();
                CompletableFuture<CountryStateCitiesResponse> value = entry.getValue();
                result.put(key,value.get().getData());
            }
            countryStatesAndCitiesData.setCountry(country);
            countryStatesAndCitiesData.setStatesAndCities(result);
            countryStatesAndCitiesResponse.setData(countryStatesAndCitiesData);
            return countryStatesAndCitiesResponse;
        }
        catch (Exception e)
        {
            throw new CustomRuntimeException(e.getMessage(),e);
        }
    }

    @Override
    public CurrencyConversionResponse convertCurrency(String country, double amount, String targetCurrency) {
        try {
            targetCurrency= targetCurrency.toUpperCase();
            CurrencyConversionResponse currencyConversionResponse= new CurrencyConversionResponse();
            CompletableFuture<CountryCurrencyResponse> countryCurrencyResponse= getCurrency(country);
            countryCurrencyResponse.join();

            String sourceCurrency =countryCurrencyResponse.get().getData().getCurrency();
            Optional<ExchangeRate>exchangeRate = exchangeRateRepository.findExchangeRatesByTargetCurrencyAndSourceCurrency(targetCurrency,sourceCurrency);
            ExchangeRate unWrappedExchangeRate= unWrapExchangeRate(exchangeRate,targetCurrency,sourceCurrency);
            currencyConversionResponse.setCountryCurrency(sourceCurrency);
            currencyConversionResponse.setConvertedAmount(unWrappedExchangeRate.getRate()*amount);
            currencyConversionResponse.setTargetCurrency(targetCurrency);
            return currencyConversionResponse;

        }catch (Exception exception)
        {
            throw new CustomRuntimeException(exception.getMessage(),exception);
        }
    }

    @Async
    public CompletableFuture<CountryLocationResponse> getLocation(String country) {
        try {
            log.info("Getting location of " + country + " "+ Thread.currentThread().getName());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(jsonObject.toString(), httpHeaders);
            sslManger.disableSSL();
            ResponseEntity<CountryLocationResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/positions",
                    HttpMethod.POST,
                    httpEntity,
                    CountryLocationResponse.class
            );

            if (responseEntity.getStatusCode().is3xxRedirection()) {
                HttpHeaders headers = responseEntity.getHeaders();
                if (headers.containsKey("Location")) {
                    String redirectUrl = headers.getFirst("Location");
                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");

                    // Make a new request to the redirect URL
                    responseEntity = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            new HttpEntity<>(null, httpHeaders),
                            CountryLocationResponse.class
                    );
                }
            }
            log.info(responseEntity.getBody());
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.info("Unable to get city population");
            throw new CustomRuntimeException(e.getMessage(),e);
        }
    }

    @Async
    public CompletableFuture<CountryCapitalResponse> getCapitalCity(String country) {
        try {
            log.info("Getting capital city of  " + country + " "+ Thread.currentThread().getName());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(jsonObject.toString(), httpHeaders);
            ResponseEntity<CountryCapitalResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/capital",
                    HttpMethod.POST,
                    httpEntity,
                    CountryCapitalResponse.class
            );
            // Check if the response status code is a redirection (3xx)
            if (responseEntity.getStatusCode().is3xxRedirection()) {
                HttpHeaders headers = responseEntity.getHeaders();
                if (headers.containsKey("Location")) {
                    String redirectUrl = headers.getFirst("Location");
                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
                    ;

                    // Make a new request to the redirect URL
                    responseEntity = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            new HttpEntity<>(null, httpHeaders),
                            CountryCapitalResponse.class
                    );
                }
            }

            log.info(responseEntity.getBody());
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.info("Unable to get city population");
            throw new CustomRuntimeException(e.getMessage(),e);        }
    }

    @Async
    public CompletableFuture<CountryPopulationResponse> getPopulation(String country) {
        try {
            log.info("Getting population of " + country + " "+ Thread.currentThread().getName());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(jsonObject.toString(), httpHeaders);
            ResponseEntity<CountryPopulationResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/population",
                    HttpMethod.POST,
                    httpEntity,
                    CountryPopulationResponse.class
            );
            // Check if the response status code is a redirection (3xx)
            if (responseEntity.getStatusCode().is3xxRedirection()) {
                HttpHeaders headers = responseEntity.getHeaders();
                if (headers.containsKey("Location")) {
                    String redirectUrl = headers.getFirst("Location");
                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
                    ;

                    // Make a new request to the redirect URL
                    responseEntity = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            new HttpEntity<>(null, httpHeaders),
                            CountryPopulationResponse.class
                    );
                }
            }

            log.info(responseEntity.getBody());
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.info("Unable to get city population");
            throw new CustomRuntimeException(e.getMessage(),e);        }
    }

    @Async
    public CompletableFuture<CountryCurrencyResponse> getCurrency(String country) {
        try {
            log.info("Getting currency of " + country + " "+ Thread.currentThread().getName());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(jsonObject.toString(), httpHeaders);
            sslManger.disableSSL();
            ResponseEntity<CountryCurrencyResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/currency",
                    HttpMethod.POST,
                    httpEntity,
                    CountryCurrencyResponse.class
            );
            // Check if the response status code is a redirection (3xx)
            if (responseEntity.getStatusCode().is3xxRedirection()) {
                HttpHeaders headers = responseEntity.getHeaders();
                if (headers.containsKey("Location")) {
                    String redirectUrl = headers.getFirst("Location");
                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
                    sslManger.disableSSL();
                    // Make a new request to the redirect URL
                    responseEntity = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            new HttpEntity<>(null, httpHeaders),
                            CountryCurrencyResponse.class
                    );
                }
            }

            log.info(responseEntity.getBody());
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.info("Unable to get city population");
            throw new CustomRuntimeException(e.getMessage(),e);        }
    }

    @Async
    public CompletableFuture<CountryStatesResponse> getState(String country) {
        try {
            log.info("Getting states in " + country + " "+ Thread.currentThread().getName());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("country", country);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(jsonObject.toString(), httpHeaders);
            ResponseEntity<CountryStatesResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/states",
                    HttpMethod.POST,
                    httpEntity,
                    CountryStatesResponse.class
            );
            // Check if the response status code is a redirection (3xx)
            if (responseEntity.getStatusCode().is3xxRedirection()) {
                HttpHeaders headers = responseEntity.getHeaders();
                if (headers.containsKey("Location")) {
                    String redirectUrl = headers.getFirst("Location");
                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
                    ;

                    // Make a new request to the redirect URL
                    responseEntity = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            new HttpEntity<>(null, httpHeaders),
                            CountryStatesResponse.class
                    );
                }
            }
            log.info(responseEntity.getBody());
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.info("Unable to get city population");
            throw new CustomRuntimeException(e.getMessage(),e);        }
    }

    @Async
    public CompletableFuture<CountryStateCitiesResponse> getCitiesInState(String country, String state) {
        try {
            log.info("Getting states in " + country+ " "+ Thread.currentThread().getName());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("country", country);
            jsonObject.put("state", state);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            HttpEntity httpEntity = new HttpEntity(jsonObject.toString(), httpHeaders);
            ResponseEntity<CountryStateCitiesResponse> responseEntity = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/state/cities",
                    HttpMethod.POST,
                    httpEntity,
                    CountryStateCitiesResponse.class
            );

            // Check if the response status code is a redirection (3xx)
            if (responseEntity.getStatusCode().is3xxRedirection()) {
                HttpHeaders headers = responseEntity.getHeaders();
                if (headers.containsKey("Location")) {
                    String redirectUrl = headers.getFirst("Location");
                    String url = "https://countriesnow.space" + URLDecoder.decode(redirectUrl, "UTF-8");
                    ;

                    // Make a new request to the redirect URL
                    responseEntity = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            new HttpEntity<>(null, httpHeaders),
                            CountryStateCitiesResponse.class
                    );
                }
            }
            log.info(responseEntity.getBody());
            return CompletableFuture.completedFuture(responseEntity.getBody());
        } catch (Exception e) {
            log.info("Unable to get city population");
            throw new CustomRuntimeException(e.getMessage(), e);
        }
    }

    static ExchangeRate unWrapExchangeRate(Optional<ExchangeRate> entity, String targetCurrency, String sourceCurrency) {
        if (entity.isPresent()) return entity.get();
        else throw new CustomRuntimeException("Could not find source currency" +sourceCurrency+ " and target currency " + targetCurrency);
    }

}
