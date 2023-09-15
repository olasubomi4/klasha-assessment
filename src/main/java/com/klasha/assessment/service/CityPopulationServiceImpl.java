package com.klasha.assessment.service;

import com.klasha.assessment.model.response.cityPopulation.CityPopulationCity;
import com.klasha.assessment.model.response.cityPopulation.CityPopulationResponse;
import com.klasha.assessment.model.response.filterCitiesAndPopulation.FilterCitiesAndPopulationResponse;
import com.klasha.assessment.utilities.ErrorMessagesConstant;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class CityPopulationServiceImpl implements CityPopulationService{
    private static Logger log = LogManager.getLogger(CityPopulationServiceImpl.class);
    private final RestTemplate restTemplate;

    @Override
    public List<CityPopulationResponse> getMostPopulatedCities(Integer numberOfCities)  {
          try {
            CompletableFuture<FilterCitiesAndPopulationResponse> newZealandCompletableFuture= getMostPopulatedCities(numberOfCities,"New zealand")
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                    }
                    );
            CompletableFuture<FilterCitiesAndPopulationResponse> italyCompletableFuture=getMostPopulatedCities(numberOfCities,"Italy")
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                    }
                    );
            CompletableFuture<FilterCitiesAndPopulationResponse> ghanaCompletableFuture=getMostPopulatedCities(numberOfCities,"Ghana")
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                    }
                    );

            //Waits for the response from the each of the
            CompletableFuture.allOf(newZealandCompletableFuture, italyCompletableFuture, ghanaCompletableFuture).join();

            List<CityPopulationResponse> cityPopulationResponseList=  new ArrayList<>();
            cityPopulationResponseList.add(generateFilterCitiesAndPopulationResponseData(numberOfCities,newZealandCompletableFuture.get(),"New zealand"));
            cityPopulationResponseList.add(generateFilterCitiesAndPopulationResponseData(numberOfCities,italyCompletableFuture.get(),"Italy"));
            cityPopulationResponseList.add(generateFilterCitiesAndPopulationResponseData(numberOfCities,ghanaCompletableFuture.get(),"Ghana"));

            return  cityPopulationResponseList;
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException();
        } catch (UnsupportedEncodingException e) {
              throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
          } catch (ExecutionException e) {
              throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
          }
          catch (RuntimeException e) {
              throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
          }
    }
    @Async("taskExecutor")
    public CompletableFuture<FilterCitiesAndPopulationResponse> getMostPopulatedCities(Integer numberOfCities,String country) throws InterruptedException, UnsupportedEncodingException {
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
            throw new RuntimeException();
        }
    }
    private CityPopulationResponse generateFilterCitiesAndPopulationResponseData(Integer numberOfCities,FilterCitiesAndPopulationResponse filterCitiesAndPopulationResponse,String country ) throws ExecutionException, InterruptedException {


        CityPopulationResponse cityPopulationResponse= new CityPopulationResponse();
        cityPopulationResponse.setCountry(country);
        List<CityPopulationCity> cityPopulationCities= new ArrayList<>();
        Integer numberOfCitiesInResponse = filterCitiesAndPopulationResponse.getDataSize();

        for (int i = 0; i <numberOfCities; i++) {
            if(i<numberOfCitiesInResponse) {
                CityPopulationCity city = new CityPopulationCity();
                city.setPopulation(filterCitiesAndPopulationResponse.getPopulationValue(i));
                city.setName(filterCitiesAndPopulationResponse.getCity(i));
                cityPopulationCities.add(city);
            }
            else
            {
                break;
            }
        }
        cityPopulationResponse.setCities(cityPopulationCities);

        return cityPopulationResponse;
    }
}
