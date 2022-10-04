package com.edu.icesi.restzooregisters.controller;

import com.edu.icesi.restzooregisters.dto.AnimalDTO;
import com.edu.icesi.restzooregisters.mapper.AnimalMapper;
import com.edu.icesi.restzooregisters.repository.AnimalRepository;
import com.edu.icesi.restzooregisters.service.AnimalService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.edu.icesi.restzooregisters.constants.GenericTurtles.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnimalControllerTest {
    private AnimalController animalController;
    private AnimalService animalService;
    private AnimalRepository animalRepository;
    private AnimalMapper animalMapper;
    private AnimalDTO animalDTO;
    private AnimalDTO updatedAnimalDTO;
    private String animalName;

    @BeforeEach
    private void init(){
        animalService = mock(AnimalService.class);
        animalMapper = mock(AnimalMapper.class);
        animalController = new AnimalController(animalService,animalMapper);
    }

    private void setupScene1(){
        animalName="Pablito";
        char sex='M';
        double weight=131;
        int age=13;
        double height=46;
        LocalDateTime arrivalDate= TURTLE_DATE;
        UUID fatherID=GENERIC_MALE_ID;
        UUID motherID=GENERIC_FEMALE_ID;
        animalDTO = new AnimalDTO(UUID.randomUUID(),animalName,sex,weight,age,height,arrivalDate,fatherID,motherID);
    }

    private void setupScene2(){
        animalName = "PablitoSSJ";
        char sex='M';
        double weight=140;
        int age=14;
        double height=47;
        LocalDateTime arrivalDate= TURTLE_DATE;
        UUID fatherID=GENERIC_MALE_ID;
        UUID motherID=GENERIC_FEMALE_ID;
        updatedAnimalDTO=new AnimalDTO();
    }

    private boolean createGeneratesException(){
        when(animalMapper.fromAnimal(any())).thenReturn(animalDTO);
        try {
            animalController.createAnimal(animalDTO);
        }
        catch (Exception e){
            return true;
        }
        return false;
    }

    private boolean updateGeneratesException(){
        when(animalMapper.fromAnimal(any())).thenReturn(animalDTO);
        try {
            animalController.updateAnimal(animalName,animalDTO);
        }
        catch (Exception e){
            return true;
        }
        return false;
    }

    @Test
    public void testCreateAnimal(){
        setupScene1();
        assertFalse(createGeneratesException());
    }

    @Test
    public void testUpdateAnimal(){
        setupScene1();
        setupScene2();
        assertFalse(updateGeneratesException());
    }

    @Test
    public void testGetAnimals(){
        animalController.getAnimals();
        verify(animalService, times(1)).getAnimals();
    }

    @Test
    public void testGetUser(){
        setupScene1();
        animalController.getAnimal(animalName);
        verify(animalService,times(1)).getAnimal(animalName);
    }

    @Test
    public void testValidateNameNoNumbers(){
        setupScene1();
        animalDTO.setName("123HolaSoyUnNombreInvalido456");
        assertTrue(createGeneratesException());
    }

    @Test
    public void testValidateNameStringSize(){
        setupScene1();
        String name = StringUtils.repeat("a", 121);
        animalDTO.setName(name);
        assertTrue(createGeneratesException());
    }

}
