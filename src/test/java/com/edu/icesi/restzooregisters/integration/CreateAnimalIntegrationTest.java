package com.edu.icesi.restzooregisters.integration;

import com.edu.icesi.restzooregisters.dto.AnimalDTO;
import com.edu.icesi.restzooregisters.error.exception.AnimalError;
import com.edu.icesi.restzooregisters.error.exception.AnimalException;
import com.edu.icesi.restzooregisters.integration.config.InitialDataConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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

import static com.edu.icesi.restzooregisters.constants.AnimalErrorCode.CODE_07;
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
public class CreateAnimalIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ANIMAL_UUID = "5631cbd3-cf53-415f-bd06-4e995ee3c322";

    @BeforeEach
    public void init(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @SneakyThrows
    public void createAnimal(){
        String body = parseResourceToString("createAnimal.json");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/zooregisters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)).andExpect(status().isOk())
                .andReturn();

        AnimalDTO animalDTO= objectMapper.readValue(result.getResponse().getContentAsString(),AnimalDTO.class);

        assertThat(animalDTO,hasProperty("name",is("Perry")));
    }

    @Test
    @SneakyThrows
    public void createAnimalRepeated(){
        String body = parseResourceToString("createAnimal.json");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/zooregisters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andExpect(status().isConflict())
                .andReturn();

        AnimalError animalError = objectMapper.readValue(result.getResponse().getContentAsString(), AnimalError.class);
        assertThat(animalError,hasProperty("code",is(CODE_07)));
    }


    @SneakyThrows
    private String parseResourceToString(String classpath) {
        Resource resource = new ClassPathResource(classpath);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

}
