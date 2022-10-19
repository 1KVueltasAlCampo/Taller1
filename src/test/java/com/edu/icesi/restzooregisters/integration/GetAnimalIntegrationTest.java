package com.edu.icesi.restzooregisters.integration;

import com.edu.icesi.restzooregisters.dto.AnimalDTO;
import com.edu.icesi.restzooregisters.error.exception.AnimalError;
import com.edu.icesi.restzooregisters.integration.config.InitialDataConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

import static com.edu.icesi.restzooregisters.constants.AnimalErrorCode.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:h2:mem:testdb" })
@Import({InitialDataConfig.class})
@ActiveProfiles("test")
public class GetAnimalIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ANIMAL_NAME = "Camila";

    @BeforeEach
    public void init(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }


    @Test
    @SneakyThrows
    public void getAnimalSuccessfully() {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/zooregisters/" + ANIMAL_NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        AnimalDTO[] animalResults = animalResults(response);


        assertThat(animalResults[0], hasProperty("name", is("Fabricio")));
        assertThat(animalResults[1], hasProperty("name", is("Amanda")));
        assertThat(animalResults[2], hasProperty("name", is("Camila")));
    }

    @Test
    @SneakyThrows
    public void getAllAnimalsSuccessfully() {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/zooregisters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        AnimalDTO[] animalResults = animalResults(response);
        assertThat(animalResults[0], hasProperty("name", is("Fabricio")));
        assertThat(animalResults[1], hasProperty("name", is("Amanda")));
        assertThat(animalResults[2], hasProperty("name", is("Camila")));
    }
    @SneakyThrows
    private AnimalDTO[] animalResults(String response){
        String[] animals = response.substring(1,response.length()-1).split("},");
        AnimalDTO[] animalsResults = new AnimalDTO[animals.length];
        for(int i =0;i<animals.length;i++){
            animals[i] = animals[i]+"}";
            animalsResults[i]= objectMapper.readValue(animals[i], AnimalDTO.class);
        }
        return animalsResults;
    }

    @Test
    @SneakyThrows
    public void testNotFound() {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/zooregisters/IMNOTINTHEDATABASE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        AnimalError animalError = objectMapper.readValue(result.getResponse().getContentAsString(), AnimalError.class);
        assertThat(animalError,hasProperty("code",is(CODE_01)));
        assertThat(animalError,hasProperty("message",is(CODE_01.getMessage())));
    }
}
