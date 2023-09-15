package com.klasha.assessment.service;

import com.klasha.assessment.entity.ExchangeRate;
import com.klasha.assessment.model.response.countryInfo.CountryInfoLocation;
import com.klasha.assessment.model.response.countryInfo.CountryInfoResponse;
import com.klasha.assessment.model.response.countryStatesAndCities.CountryStatesAndCitiesResponse;
import com.klasha.assessment.model.response.currencyConversion.CurrencyConversionResponse;
import com.klasha.assessment.repository.ExchangeRateRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountryInfoServiceTest {

    @Mock
    ExchangeRateRepository exchangeRateRepository;
    @InjectMocks
    CountryInfoService countryInfoService= new CountryInfoServiceImpl(new RestTemplate(),exchangeRateRepository);


    @Test
    public void testCovertCurrency_success() {
        String targetCurrency="NGN";
        String sourceCurrency="USD";
        String country= "United States";
        BigDecimal amount = new BigDecimal(200);

        ExchangeRate exchangeRate= new com.klasha.assessment.entity.ExchangeRate();
        exchangeRate.setRate(new BigDecimal(460.72));
        exchangeRate.setId(1);
        exchangeRate.setCreatedAt(new Timestamp( Instant.now().toEpochMilli()));
        exchangeRate.setUpdatedAt(new Timestamp( Instant.now().toEpochMilli()));
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setSourceCurrency(sourceCurrency);

        CurrencyConversionResponse expected= new CurrencyConversionResponse();
        expected.setCountryCurrency("USD");
        expected.setTargetCurrency("NGN");
        expected.setConvertedAmount(exchangeRate.getRate().multiply(amount));

        when(exchangeRateRepository.findExchangeRatesByTargetCurrencyAndSourceCurrency(targetCurrency,sourceCurrency)).thenReturn(Optional.of(exchangeRate));
        CurrencyConversionResponse result = countryInfoService.convertCurrency(country,amount,targetCurrency);

        assertEquals(expected,result);
    }

    @Test
    public void testCovertCurrency_errorInvalidCountry() {
        String targetCurrency="NGN";
        String sourceCurrency="USD";
        String country= "United Statesdd";
        BigDecimal amount = new BigDecimal(200);

        //Building expected result
        ExchangeRate exchangeRate= new com.klasha.assessment.entity.ExchangeRate();
        exchangeRate.setRate(new BigDecimal(460.72));
        exchangeRate.setId(1);
        exchangeRate.setCreatedAt(new Timestamp( Instant.now().toEpochMilli()));
        exchangeRate.setUpdatedAt(new Timestamp( Instant.now().toEpochMilli()));
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setSourceCurrency(sourceCurrency);

        //Building mock entity response
        CurrencyConversionResponse expected= new CurrencyConversionResponse();
        expected.setCountryCurrency("USD");
        expected.setTargetCurrency("NGN");
        expected.setConvertedAmount(exchangeRate.getRate().multiply(amount));
        when(exchangeRateRepository.findExchangeRatesByTargetCurrencyAndSourceCurrency(targetCurrency,sourceCurrency)).thenReturn(Optional.of(exchangeRate));

        //Verifying the result
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(()->
        {
            //Performing the test
            countryInfoService.convertCurrency(country,amount,targetCurrency);
        }
        ).withMessage("An unexpected error occurred while processing your request. Please try again later.");

    }

    @Test
    public void testCovertCurrency_errorTargetCurrencyThatDo() {
        String targetCurrency="NGNdw";
        String sourceCurrency="USD";
        String country= "United States";
        BigDecimal amount = new BigDecimal(200);

        //Building expected response
        ExchangeRate exchangeRate= new com.klasha.assessment.entity.ExchangeRate();
        exchangeRate.setRate(new BigDecimal(460.72));
        exchangeRate.setId(1);
        exchangeRate.setCreatedAt(new Timestamp( Instant.now().toEpochMilli()));
        exchangeRate.setUpdatedAt(new Timestamp( Instant.now().toEpochMilli()));
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setSourceCurrency(sourceCurrency);

        //Building mock entity response
        CurrencyConversionResponse expected= new CurrencyConversionResponse();
        expected.setCountryCurrency("USD");
        expected.setTargetCurrency("NGN");
        expected.setConvertedAmount(exchangeRate.getRate().multiply(amount));
        when(exchangeRateRepository.findExchangeRatesByTargetCurrencyAndSourceCurrency(targetCurrency,sourceCurrency)).thenReturn(Optional.empty());

        // Verifying the result
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(()->
                {
                    // Performing the test
                    countryInfoService.convertCurrency(country,amount,targetCurrency);
                }
        ).withMessage("Currency conversion not available for the United States and "+ targetCurrency+" Please check the provided country and currency codes, and ensure they are valid for conversion");
    }


    @Test
    public void testGetCountryInfo_success() {
        String country= "United States";

        //Building expected response
        CountryInfoResponse expected = new CountryInfoResponse();
        expected.setCurrency("USD");
        expected.setIso2("US");
        expected.setIso3("USA");
        expected.setCountry("United States");
        expected.setPopulation(326687501);
        expected.setCapital("Washington");
        CountryInfoLocation countryInfoLocation= new CountryInfoLocation();
        countryInfoLocation.setLongitude(-97.0);
        countryInfoLocation.setLatitude(38.0);
        expected.setLocation(countryInfoLocation);

        // Perform the test
        CountryInfoResponse result=  countryInfoService.getCountryInfo(country);

        // Verify the result
        assertEquals(expected,result);

    }

    @Test
    public void testGetCountryInfo_errorWithInvalidCountry() {
        String invalidCountry= "United Starw";

        //Building expected response
        CountryInfoResponse expected = new CountryInfoResponse();
        expected.setCurrency("USD");
        expected.setIso2("US");
        expected.setIso3("USA");
        expected.setCountry("United States");
        expected.setPopulation(326687501);
        expected.setCapital("Washington");
        CountryInfoLocation countryInfoLocation= new CountryInfoLocation();
        countryInfoLocation.setLongitude(-97.0);
        countryInfoLocation.setLatitude(38.0);
        expected.setLocation(countryInfoLocation);

        // Verify the result
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(()->
                {
                    // Performing the test
                    CountryInfoResponse result=  countryInfoService.getCountryInfo(invalidCountry);
                }
        ).withMessage("An unexpected error occurred while processing your request. Please try again later.");
    }



    @Test
    public void testGetStatesAndCitiesByCountry_success() {
        String validCountry= "Nauru";

        //Building expected response
        CountryStatesAndCitiesResponse expected = new CountryStatesAndCitiesResponse();
        expected.setCountry(validCountry);

        Map<String, List<String>>  statesAndCities= new HashMap<>();
        List<String> expectedStatesInAiwoDistrict= Arrays.asList("Arijejen");
        statesAndCities.put("Aiwo District",expectedStatesInAiwoDistrict);

        List<String> expectedStatesInNibokDistric= Arrays.asList();
        statesAndCities.put("Nibok District",expectedStatesInNibokDistric);

        List<String> expectedStatesInBoeDistrict= Arrays.asList();
        statesAndCities.put("Boe District",expectedStatesInBoeDistrict);

        List<String> expectedStatesInAnabarDistrict= Arrays.asList("Anabar");
        statesAndCities.put("Anabar District",expectedStatesInAnabarDistrict);

        List<String> expectedStatesInBuadaDistrict= Arrays.asList();
        statesAndCities.put("Buada District",expectedStatesInBuadaDistrict);

        List<String> expectedStatesInUaboeDistrict= Arrays.asList("Uaboe");
        statesAndCities.put("Uaboe District",expectedStatesInUaboeDistrict);

        List<String> expectedStatesInAnetanDistrict= Arrays.asList();
        statesAndCities.put("Anetan District",expectedStatesInAnetanDistrict);

        List<String> expectedStatesInAnibareDistrict= Arrays.asList();
        statesAndCities.put("Anibare District",expectedStatesInAnibareDistrict);

        List<String> expectedStatesInMenengDistrict= Arrays.asList("Menen");
        statesAndCities.put("Meneng District",expectedStatesInMenengDistrict);

        List<String> expectedStatesInEwaDistrict= Arrays.asList("");
        statesAndCities.put("Ewa District",expectedStatesInEwaDistrict);

        List<String> expectedStatesInDenigomoduDistrict= Arrays.asList("");
        statesAndCities.put("Denigomodu District",expectedStatesInDenigomoduDistrict);

        List<String> expectedStatesInBaitiDistrict= Arrays.asList("Baiti");
        statesAndCities.put("Baiti District",expectedStatesInBaitiDistrict);

        List<String> expectedStatesInIjuwDistrict= Arrays.asList("Ijuw");
        statesAndCities.put("Ijuw District",expectedStatesInIjuwDistrict);

        List<String> expectedStatesInYarenDistrict= Arrays.asList("Yaren");
        statesAndCities.put("Yaren District",expectedStatesInYarenDistrict);

        expected.setStatesAndCities(statesAndCities);

        // Perform the test
        CountryStatesAndCitiesResponse result=  countryInfoService.getStatesAndCitiesByCountry(validCountry);

        // Verify the result
        assertEquals(expected,result);

    }


    @Test
    public void testGetStatesAndCitiesByCountry_errorInvalidCountry() {
        String invalidCountry= "Naururr";

        //Building expected response
        CountryStatesAndCitiesResponse expected = new CountryStatesAndCitiesResponse();
        expected.setCountry(invalidCountry);

        Map<String, List<String>>  statesAndCities= new HashMap<>();
        List<String> expectedStatesInAiwoDistrict= Arrays.asList("Arijejen");
        statesAndCities.put("Aiwo District",expectedStatesInAiwoDistrict);

        List<String> expectedStatesInNibokDistric= Arrays.asList();
        statesAndCities.put("Nibok District",expectedStatesInNibokDistric);

        List<String> expectedStatesInBoeDistrict= Arrays.asList();
        statesAndCities.put("Boe District",expectedStatesInBoeDistrict);

        List<String> expectedStatesInAnabarDistrict= Arrays.asList("Anabar");
        statesAndCities.put("Anabar District",expectedStatesInAnabarDistrict);

        List<String> expectedStatesInBuadaDistrict= Arrays.asList();
        statesAndCities.put("Buada District",expectedStatesInBuadaDistrict);

        List<String> expectedStatesInUaboeDistrict= Arrays.asList("Uaboe");
        statesAndCities.put("Uaboe District",expectedStatesInUaboeDistrict);

        List<String> expectedStatesInAnetanDistrict= Arrays.asList();
        statesAndCities.put("Anetan District",expectedStatesInAnetanDistrict);

        List<String> expectedStatesInAnibareDistrict= Arrays.asList();
        statesAndCities.put("Anibare District",expectedStatesInAnibareDistrict);

        List<String> expectedStatesInMenengDistrict= Arrays.asList("Menen");
        statesAndCities.put("Meneng District",expectedStatesInMenengDistrict);

        List<String> expectedStatesInEwaDistrict= Arrays.asList("");
        statesAndCities.put("Ewa District",expectedStatesInEwaDistrict);

        List<String> expectedStatesInDenigomoduDistrict= Arrays.asList("");
        statesAndCities.put("Denigomodu District",expectedStatesInDenigomoduDistrict);

        List<String> expectedStatesInBaitiDistrict= Arrays.asList("Baiti");
        statesAndCities.put("Baiti District",expectedStatesInBaitiDistrict);

        List<String> expectedStatesInIjuwDistrict= Arrays.asList("Ijuw");
        statesAndCities.put("Ijuw District",expectedStatesInIjuwDistrict);

        List<String> expectedStatesInYarenDistrict= Arrays.asList("Yaren");
        statesAndCities.put("Yaren District",expectedStatesInYarenDistrict);

        expected.setStatesAndCities(statesAndCities);

        // Verify the result
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(()->
                {
                    // Performing the test
                    CountryStatesAndCitiesResponse result=  countryInfoService.getStatesAndCitiesByCountry(invalidCountry);
                }
        ).withMessage("An unexpected error occurred while processing your request. Please try again later.");
    }
}
