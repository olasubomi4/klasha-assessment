package com.klasha.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klasha.assessment.model.response.cityPopulation.CityPopulationCity;
import com.klasha.assessment.model.response.cityPopulation.CityPopulationResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AssessmentApplicationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public MockMvc mockMvc;



    @Test
    public void getMostPopulatedCitiesTest_Success() throws Exception {
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

        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/most-populated-cities?numberOfCities=2");
        MvcResult result= mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(expectedResponse, result.getResponse());


    }
}
