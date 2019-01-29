package com.thefa.audit.controller;

import com.thefa.audit.config.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReferenceControllerTest extends AbstractIntegrationTest {

    @Test
    public void givenCountries_whenGetCountries_thenReturnCorrectNumberOfCountries() throws Exception {

        mvc.perform(get("/reference/countries")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(211)));

    }

    @Test
    public void givenGrades_whenGetGrades_thenReturnCorrectNumberOfGrades() throws Exception {

        mvc.perform(get("/reference/grades")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)));

    }

    @Test
    public void givenClubs_whenGetGrades_thenReturnCorrectNumberOfClubs() throws Exception {

        mvc.perform(get("/reference/clubs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2279)));
    }
}
