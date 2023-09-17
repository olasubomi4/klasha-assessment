package com.klasha.assessment.service;

import com.klasha.assessment.model.response.cityPopulation.CityPopulationCity;
import com.klasha.assessment.model.response.cityPopulation.CityPopulationResponse;
import com.klasha.assessment.model.response.filterCitiesAndPopulation.FilterCitiesAndPopulationResponse;
import com.klasha.assessment.utilities.CityPopulationServiceRestAsync;
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
    private CityPopulationServiceRestAsync cityPopulationServiceRestAsync;

    @Override
    public List<CityPopulationResponse> getMostPopulatedCities(Integer numberOfCities)  {
          try {

              log.info("opening a new thread to get populated cities in New zealand");
            CompletableFuture<FilterCitiesAndPopulationResponse> newZealandCompletableFuture= cityPopulationServiceRestAsync.getMostPopulatedCities(numberOfCities,"New zealand")
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                    }
                    );

              log.info("opening a new thread to get populated cities in Italy");
              CompletableFuture<FilterCitiesAndPopulationResponse> italyCompletableFuture=cityPopulationServiceRestAsync.getMostPopulatedCities(numberOfCities,"Italy")
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                    }
                    );

              log.info("opening a new thread to get populated cities in Ghana");
              CompletableFuture<FilterCitiesAndPopulationResponse> ghanaCompletableFuture=cityPopulationServiceRestAsync.getMostPopulatedCities(numberOfCities,"Ghana")
                    .exceptionally(ex ->
                    {
                        throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
                    }
                    );

            log.info("Waiting for all CompletableFuture instances to complete then join (block) until all CompletableFuture instances are completed");
            CompletableFuture.allOf(newZealandCompletableFuture, italyCompletableFuture, ghanaCompletableFuture).join();

            List<CityPopulationResponse> cityPopulationResponseList=  new ArrayList<>();
            cityPopulationResponseList.add(generateFilterCitiesAndPopulationResponseData(numberOfCities,newZealandCompletableFuture.get(),"New zealand"));
            cityPopulationResponseList.add(generateFilterCitiesAndPopulationResponseData(numberOfCities,italyCompletableFuture.get(),"Italy"));
            cityPopulationResponseList.add(generateFilterCitiesAndPopulationResponseData(numberOfCities,ghanaCompletableFuture.get(),"Ghana"));

            return  cityPopulationResponseList;
        }
        catch (InterruptedException e)
        {
            log.error(e,e);
            throw new RuntimeException();
        } catch (UnsupportedEncodingException e) {
              throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
          } catch (ExecutionException e) {
              log.error(e,e);
              throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
          }
          catch (RuntimeException e) {
              log.error(e,e);
              throw new RuntimeException(ErrorMessagesConstant.UNABLE_TO_PROCESS_REQUEST_AT_THE_MOMENT);
          }
    }

    private CityPopulationResponse generateFilterCitiesAndPopulationResponseData(Integer numberOfCities,FilterCitiesAndPopulationResponse filterCitiesAndPopulationResponse,String country ) throws ExecutionException, InterruptedException {
        log.info("mapping filterCitiesAndPopulationResponse object to cityPopulationResponse");

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
