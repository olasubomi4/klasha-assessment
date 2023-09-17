package com.klasha.assessment.service;

import com.klasha.assessment.model.response.cityPopulation.CityPopulationCity;
import com.klasha.assessment.model.response.cityPopulation.CityPopulationResponse;
import com.klasha.assessment.utilities.CityPopulationServiceRestAsync;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CityPopulationServiceTest {
    
    @InjectMocks
    CityPopulationService cityPopulationService= new CityPopulationServiceImpl(new RestTemplate(),new CityPopulationServiceRestAsync(new RestTemplate()));

    @Test
    public void testMostPopulatedCitiesInNewZealandGhanaItaly_success()
    {
        CityPopulationResponse ghana= new CityPopulationResponse();
        ghana.setCountry("Ghana");
        List<CityPopulationCity> ghanaCitites = new ArrayList<CityPopulationCity>();

        CityPopulationCity accra= new CityPopulationCity();
        accra.setPopulation(1658937.0);
        accra.setName("ACCRA");

        CityPopulationCity kumasi= new CityPopulationCity();
        kumasi.setPopulation(1170270.0);
        kumasi.setName("Kumasi");

        ghanaCitites.add(accra);
        ghanaCitites.add(kumasi);
        ghana.setCities(ghanaCitites);


        CityPopulationResponse italy= new CityPopulationResponse();
        italy.setCountry("Italy");

        List<CityPopulationCity> italyCitites = new ArrayList<CityPopulationCity>();

        CityPopulationCity roma= new CityPopulationCity();
        roma.setName("ROMA");
        roma.setPopulation(2626553.0);

        CityPopulationCity milano= new CityPopulationCity();
        milano.setPopulation(1251137.0);
        milano.setName("Milano");

        italyCitites.add(roma);
        italyCitites.add(milano);

        italy.setCities(italyCitites);


        CityPopulationResponse newZealand= new CityPopulationResponse();
        newZealand.setCountry("New zealand");

        List<CityPopulationCity> newZealandCitites = new ArrayList<CityPopulationCity>();

        CityPopulationCity auckland= new CityPopulationCity();
        auckland.setName("Auckland");
        auckland.setPopulation(1529400.0);

        CityPopulationCity manukau= new CityPopulationCity();
        manukau.setName("Manukau");
        manukau.setPopulation(375700.0);

        newZealandCitites.add(auckland);
        newZealandCitites.add(manukau);


        newZealand.setCities(newZealandCitites);



        List<CityPopulationResponse> expectedResponse = new ArrayList<>();
        expectedResponse.add(newZealand);
        expectedResponse.add(italy);
        expectedResponse.add(ghana);

        List<CityPopulationResponse> result = cityPopulationService.getMostPopulatedCities(2);

        assertEquals(expectedResponse,result);

    }

    @Test
    public void testMostPopulatedCitiesInNewZealandGhanaItaly()
    {
        CityPopulationResponse ghana= new CityPopulationResponse();
        ghana.setCountry("Ghana");
        List<CityPopulationCity> ghanaCitites = new ArrayList<CityPopulationCity>();

        CityPopulationCity accra= new CityPopulationCity();
        accra.setPopulation(1658937.0);
        accra.setName("ACCRA");

        CityPopulationCity kumasi= new CityPopulationCity();
        kumasi.setPopulation(1170270.0);
        kumasi.setName("Kumasi");

        ghanaCitites.add(accra);
        ghanaCitites.add(kumasi);
        ghana.setCities(ghanaCitites);

        CityPopulationResponse italy= new CityPopulationResponse();
        italy.setCountry("Italy");

        List<CityPopulationCity> italyCitites = new ArrayList<CityPopulationCity>();

        CityPopulationCity roma= new CityPopulationCity();
        roma.setName("ROMA");
        roma.setPopulation(2626553.0);

        CityPopulationCity milano= new CityPopulationCity();
        milano.setPopulation(1251137.0);
        milano.setName("Milano");

        italyCitites.add(roma);
        italyCitites.add(milano);
        italy.setCities(italyCitites);


        CityPopulationResponse newZealand= new CityPopulationResponse();
        newZealand.setCountry("New zealand");

        List<CityPopulationCity> newZealandCitites = new ArrayList<CityPopulationCity>();

        CityPopulationCity auckland= new CityPopulationCity();
        auckland.setName("Auckland");
        auckland.setPopulation(1529400.0);

        CityPopulationCity manukau= new CityPopulationCity();
        manukau.setName("Manukau");
        manukau.setPopulation(375700.0);

        newZealandCitites.add(auckland);
        newZealandCitites.add(manukau);
        newZealand.setCities(newZealandCitites);


        List<CityPopulationResponse> expectedResponse = new ArrayList<>();
        expectedResponse.add(newZealand);
        expectedResponse.add(italy);
        expectedResponse.add(ghana);

        List<CityPopulationResponse> result = cityPopulationService.getMostPopulatedCities(2);

        assertEquals(expectedResponse,result);

    }

    @Test
    public void testMostPopulatedCitiesInNewZealandGhanaItaly_successWithNumberOfCitiesLessThan1()
    {
        CityPopulationResponse ghana= new CityPopulationResponse();
        ghana.setCountry("Ghana");
        ghana.setCities(new ArrayList<CityPopulationCity>());


        CityPopulationResponse italy= new CityPopulationResponse();
        italy.setCountry("Italy");
        italy.setCities(new ArrayList<CityPopulationCity>());

        CityPopulationResponse newZealand= new CityPopulationResponse();
        newZealand.setCountry("New zealand");
        newZealand.setCities(new ArrayList<CityPopulationCity>());

        List<CityPopulationResponse> expectedResponse = new ArrayList<>();
        expectedResponse.add(newZealand);
        expectedResponse.add(italy);
        expectedResponse.add(ghana);

        List<CityPopulationResponse> result = cityPopulationService.getMostPopulatedCities(-1);

        assertEquals(expectedResponse,result);

    }
}
