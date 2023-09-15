//package com.klasha.assessment;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.klasha.assessment.model.response.currencyConversion.CurrencyConversionResponse;
//import com.klasha.assessment.repository.ExchangeRateRepository;
//import com.klasha.assessment.service.CityPopulationService;
//import com.klasha.assessment.service.CityPopulationServiceImpl;
//import com.klasha.assessment.service.CountryInfoService;
//import com.klasha.assessment.service.CountryInfoServiceImpl;
//import org.json.JSONObject;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.web.client.RestTemplate;
//
//import java.sql.Timestamp;
//import java.time.Instant;
//import java.util.Optional;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//
//
//@RunWith(MockitoJUnitRunner.class)
//public class AssessmentApplicationUnitTest {
//    @Mock
//    ExchangeRateRepository exchangeRateRepository;
//    @InjectMocks
//    CountryInfoService countryInfoService= new CountryInfoServiceImpl(new RestTemplate(),exchangeRateRepository);
//
//    @InjectMocks
//    CityPopulationService cityPopulationService= new CityPopulationServiceImpl(new RestTemplate());
//
//    @Test
//    public void covertCurrencyTest() {
//
//        String targetCurrency="NGN";
//        String sourceCurrency="USD";
//        String country= "United States";
//        double amount =200;
//        com.klasha.assessment.entity.ExchangeRate exchangeRate= new com.klasha.assessment.entity.ExchangeRate();
//        exchangeRate.setRate(460.72);
//        exchangeRate.setId(1);
//        exchangeRate.setCreatedAt(new Timestamp( Instant.now().toEpochMilli()));
//        exchangeRate.setUpdatedAt(new Timestamp( Instant.now().toEpochMilli()));
//        exchangeRate.setTargetCurrency(targetCurrency);
//        exchangeRate.setSourceCurrency(sourceCurrency);
//        when(exchangeRateRepository.findExchangeRatesByTargetCurrencyAndSourceCurrency(targetCurrency,sourceCurrency)).thenReturn(Optional.of(exchangeRate));
//        CurrencyConversionResponse result = countryInfoService.convertCurrency(country,amount,targetCurrency);
//        JSONObject jsonObject= new JSONObject();
//        jsonObject.put("countryCurrency","USD");
//        jsonObject.put("convertedAmount", 92144.0);
//        jsonObject.put("targetCurrency", "NGN");
//
//        assertEquals(jsonObject.toString(),result.toString());
//    }
//
//    @Test
//    public void input_0_number_of_cities_when_trying_to_get_most_populated_cities_in_newZealand_ghana_italy() throws JsonProcessingException {
////        CityPopulationResponse expectedResponse= new CityPopulationResponse();
////        expectedResponse.setMsg("Top 0most populated cities in Italy, New Zealand, and Ghana");
////        expectedResponse.setError("false");
////        CityPopulationResponseData ghana= new CityPopulationResponseData();
////        ghana.setCountry("Ghana");
////        ghana.setCities(new ArrayList<CityPopulationResponseDataCities>());
////        CityPopulationResponseData italy= new CityPopulationResponseData();
////        italy.setCountry("Italy");
////        italy.setCities((new ArrayList<CityPopulationResponseDataCities>()));
////        CityPopulationResponseData newZealand= new CityPopulationResponseData();
////        newZealand.setCountry("New zealand");
////        newZealand.setCities((new ArrayList<CityPopulationResponseDataCities>()));
////        List<CityPopulationResponseData> cityPopulationResponseDataList = new ArrayList<>();
////        cityPopulationResponseDataList.add(newZealand);
////        cityPopulationResponseDataList.add(italy);
////        cityPopulationResponseDataList.add(ghana);
////        expectedResponse.setData(cityPopulationResponseDataList);
////
////        CityPopulationResponse result = cityPopulationService.getMostPopulatedCities(0);
////        String ExpectedResponseString ="{\"error\":\"false\",\"msg\":\"Top0mostpopulatedcitiesinItaly,NewZealand,andGhana\",\"data\":[{\"country\":\"Newzealand\",\"cities\":[]},{\"country\":\"Italy\",\"cities\":[]},{\"country\":\"Ghana\",\"cities\":[]}]}";
////
////        assertEquals(expectedResponse,result);
//    }
//
//
//    @Test
//    public void covertCurrencyWithInvalidTargetCurrencyTest() {
//
//        String targetCurrency="NG321";
//        String sourceCurrency="USD";
//        String country= "United States232";
//        double amount =200;
//        com.klasha.assessment.entity.ExchangeRate exchangeRate= new com.klasha.assessment.entity.ExchangeRate();
//        exchangeRate.setRate(460.72);
//        exchangeRate.setId(1);
//        exchangeRate.setCreatedAt(new Timestamp( Instant.now().toEpochMilli()));
//        exchangeRate.setUpdatedAt(new Timestamp( Instant.now().toEpochMilli()));
//        exchangeRate.setTargetCurrency(targetCurrency);
//        exchangeRate.setSourceCurrency(sourceCurrency);
//        when(exchangeRateRepository.findExchangeRatesByTargetCurrencyAndSourceCurrency(targetCurrency,sourceCurrency)).thenReturn(Optional.of(exchangeRate));
//
//        Throwable exception = assertThrows(CustomRuntimeException.class, () -> {
//            countryInfoService.convertCurrency(country,amount,targetCurrency);
//        });
//        // Assert - assert that the exception is thrown
//        assertNotNull(exception);
//        assertEquals("404 Not Found: \"{\"error\":true,\"msg\":\"country not found\"}", "404 Not Found: \"{\"error\":true,\"msg\":\"country not found\"}");
//
//    }
//}
