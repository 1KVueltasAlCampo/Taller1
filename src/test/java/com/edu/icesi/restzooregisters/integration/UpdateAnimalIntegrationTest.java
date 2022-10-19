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
public class UpdateAnimalIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ANIMAL_NAME = "Camila";
    private static final String ANIMAL_FATHER_UUID = "34366239-3264-3430-2d34-6662642d3131";
    private static final String ANIMAL_MOTHER_UUID = "38623937-6563-3538-2d34-6662642d3131";


    @BeforeEach
    public void init(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @SneakyThrows
    public void updateAnimal(){ //IS OK
        String body = parseResourceToString("updateAnimal.json");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/zooregisters/"+ANIMAL_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andExpect(status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        AnimalDTO animalDTO= objectMapper.readValue(result.getResponse().getContentAsString(),AnimalDTO.class);

        assertThat(animalDTO,hasProperty("age",is(15)));
        assertThat(animalDTO,hasProperty("weight",is(128.0)));
        assertThat(animalDTO,hasProperty("height",is(49.0)));
    }

    @Test
    @SneakyThrows
    public void testNotFound(){
        String body = parseResourceToString("updateAnimal.json");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/zooregisters/IMNOTINTHEDATABASE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andExpect(status().isBadRequest())
                .andReturn();

        AnimalError animalError = objectMapper.readValue(result.getResponse().getContentAsString(), AnimalError.class);
        assertThat(animalError,hasProperty("code",is(CODE_01)));
        assertThat(animalError,hasProperty("message",is(CODE_01.getMessage())));
    }


    @Test
    @SneakyThrows
    public void testFatherIsNotFemale(){
        AnimalDTO baseAnimal = baseAnimal();
        baseAnimal.setFatherID(UUID.fromString(ANIMAL_MOTHER_UUID));
        String body = objectMapper.writeValueAsString(baseAnimal);
        MvcResult result = getBadRequestResult(body);

        AnimalError animalError = objectMapper.readValue(result.getResponse().getContentAsString(), AnimalError.class);
        assertThat(animalError,hasProperty("code",is(CODE_08)));
        assertThat(animalError,hasProperty("message",is(CODE_08.getMessage())));
    }

    @Test
    @SneakyThrows
    public void testMotherIsNotMale(){
        AnimalDTO baseAnimal = baseAnimal();
        baseAnimal.setMotherID(UUID.fromString(ANIMAL_FATHER_UUID));
        String body = objectMapper.writeValueAsString(baseAnimal);
        MvcResult result = getBadRequestResult(body);

        AnimalError animalError = objectMapper.readValue(result.getResponse().getContentAsString(), AnimalError.class);
        assertThat(animalError,hasProperty("code",is(CODE_08)));
        assertThat(animalError,hasProperty("message",is(CODE_08.getMessage())));
    }

    @Test
    @SneakyThrows
    public void testNoSexChange(){
        AnimalDTO baseAnimal = baseAnimal();
        baseAnimal.setSex('M');
        String body = objectMapper.writeValueAsString(baseAnimal);
        MvcResult result = getBadRequestResult(body);

        AnimalError animalError = objectMapper.readValue(result.getResponse().getContentAsString(), AnimalError.class);
        assertThat(animalError,hasProperty("code",is(CODE_09)));
        assertThat(animalError,hasProperty("message",is(CODE_09.getMessage())));
    }

    @SneakyThrows
    private MvcResult getBadRequestResult(String body){
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/zooregisters/"+ANIMAL_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andExpect(status().isBadRequest())
                .andReturn();
        return result;
    }

    @SneakyThrows
    private String parseResourceToString(String classpath) {
        Resource resource = new ClassPathResource(classpath);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    @SneakyThrows
    private AnimalDTO baseAnimal(){
        String body = parseResourceToString("updateAnimal.json");
        return objectMapper.readValue(body, AnimalDTO.class);
    }

}
