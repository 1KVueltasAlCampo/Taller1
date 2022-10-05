package com.edu.icesi.restzooregisters.service.impl;

import com.edu.icesi.restzooregisters.dto.AnimalDTO;
import com.edu.icesi.restzooregisters.model.Animal;
import com.edu.icesi.restzooregisters.repository.AnimalRepository;
import com.edu.icesi.restzooregisters.service.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.edu.icesi.restzooregisters.constants.GenericTurtles.*;
import static com.edu.icesi.restzooregisters.constants.GenericTurtles.GENERIC_FEMALE_ID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AnimalServiceTest {

    private AnimalRepository animalRepository;
    private AnimalService animalService;
    private String animalName;
    private Animal animal;
    private Animal updatedAnimal;

    @BeforeEach
    private void init() {
        animalRepository = mock(AnimalRepository.class);
        animalService = new AnimalServiceImpl(animalRepository);
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
        animal = new Animal(UUID.randomUUID(),animalName,sex,weight,age,height,arrivalDate,fatherID,motherID);
    }

    private void setupScene2(){
        String name = "PablitoSSJ";
        char sex='M';
        double weight=140;
        int age=14;
        double height=47;
        LocalDateTime arrivalDate= TURTLE_DATE;
        UUID fatherID=GENERIC_MALE_ID;
        UUID motherID=GENERIC_FEMALE_ID;
        updatedAnimal=new Animal(UUID.randomUUID(),name,sex,weight,age,height,arrivalDate,fatherID,motherID);
    }

    private boolean createGeneratesException(){
        try {
            animalService.createAnimal(animal);
        }
        catch (Exception e){
            return true;
        }
        return false;
    }

    @Test
    public void verifyCreateAnimal(){
        setupScene1();
        assertFalse(createGeneratesException());
        verify(animalRepository,times(1)).save(any());
    }

    
}
