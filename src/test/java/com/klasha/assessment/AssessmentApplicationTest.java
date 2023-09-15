package com.klasha.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klasha.assessment.entity.User;
import com.klasha.assessment.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AssessmentApplicationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();
    private User testUser;
    private String jwtToken;

    private void generateTestUser()
    {
        User testUser= new User();
        testUser.setUsername("test");
        testUser.setPassword(bCryptPasswordEncoder.encode("superman"));
        this.testUser=testUser;
    }

    private void generateJwtToken() throws Exception {

        User testUser= new User();
        testUser.setUsername("test");
        testUser.setPassword("superman");

        RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/user/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser));
        MvcResult loginResult =  mockMvc.perform(request).andReturn();;

        // Extract the bearer token from the login response
        this.jwtToken= loginResult.getResponse().getHeader("Authorization");
    }
    @BeforeEach
    void setup() throws Exception {
        generateTestUser();

        userRepository.save(testUser);
        generateJwtToken();
    }

    @AfterEach
    void clear(){
        userRepository.delete(testUser);
    }

    @Test
    public void getMostPopulatedCitiesTest_Success() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/most-populated-cities?numberOfCities=2")
                .header("Authorization",  this.jwtToken);
                MvcResult result= mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

    }

    @Test
    public void testRegisterUser_success() throws Exception {

        User newUser= new User();
        newUser.setUsername("test3");
        newUser.setPassword("superman");
        RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser));

        MvcResult result= mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        userRepository.delete(newUser);
    }

    @Test
    public void testRegisterUser_error() throws Exception {

        User newUser= new User();
        newUser.setUsername("");
        newUser.setPassword("superman");
        RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser));

        MvcResult result= mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    @Test
    public void testMostPopulatedCitiesInNewZealandGhanaItaly_successWithNumberOfCitiesLessThan1() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/most-populated-cities?numberOfCities=0").header("Authorization",  this.jwtToken);;

        MvcResult result= mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    @Test
    public void testMostPopulatedCitiesInNewZealandGhanaItaly_success() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/most-populated-cities?numberOfCities=3") .header("Authorization",  this.jwtToken);

        MvcResult result= mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    @Test
    public void testCovertCurrency_errorInvalidCountry() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/country-info/currency-converter/convert?amount=1&targetCurrency=USD&country=United Statesdd")
                .header("Authorization",  this.jwtToken);

        MvcResult result= mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    @Test
    public void testCovertCurrency_errorTargetCurrencyThatDo() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/country-info/currency-converter/convert?amount=1&targetCurrency=USDr&country=United States")
                .header("Authorization",  this.jwtToken);

        MvcResult result= mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    @Test
    public void testGetStatesAndCitiesByCountry_errorInvalidCountry() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/country-info/details/states-and-cities?country=Nauruu")
                .header("Authorization",  this.jwtToken);

        MvcResult result= mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    @Test
    public void testGetStatesAndCitiesByCountry_success() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/country-info/details/states-and-cities?country=Nauru")
                .header("Authorization",  this.jwtToken);

        MvcResult result= mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

}
