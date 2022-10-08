package com.edu.icesi.restzooregisters.service.impl;

import com.edu.icesi.restzooregisters.dto.AnimalDTO;
import com.edu.icesi.restzooregisters.model.Animal;
import com.edu.icesi.restzooregisters.repository.AnimalRepository;
import com.edu.icesi.restzooregisters.service.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private Animal femaleAnimal;

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

    private void setupScene3(){
        String name = "Camila";
        char sex='F';
        double weight=140;
        int age=17;
        double height=47;
        LocalDateTime arrivalDate= TURTLE_DATE;
        UUID fatherID=GENERIC_MALE_ID;
        UUID motherID=GENERIC_FEMALE_ID;
        femaleAnimal=new Animal(UUID.randomUUID(),name,sex,weight,age,height,arrivalDate,fatherID,motherID);
    }

    private List<Animal> fakeAnimalList(){
        setupScene1();
        List<Animal> fakeAnimals = new ArrayList<>();
        fakeAnimals.add(animal);
        return  fakeAnimals;
    }



    private boolean createGeneratesException(){
        setupScene1();
        try {
            animalService.createAnimal(animal);
        }
        catch (Exception e){
            return true;
        }
        return false;
    }

    private boolean updateGeneratesException(){
        setupScene2();
        when(animalRepository.findAll()).thenReturn(fakeAnimalList());
        try {
            animalService.updateAnimal(animalName,updatedAnimal);
        }
        catch (Exception e){
            return true;
        }
        return false;
    }

    @Test
    public void testCreateAnimal(){
        assertFalse(createGeneratesException());
        verify(animalRepository,times(1)).save(any());
    }

    @Test
    public void testUpdateAnimal(){
        assertFalse(updateGeneratesException());
        verify(animalRepository,times(1)).save(any());
    }

    @Test
    public void testGetAnimal() {
        setupScene1();
        when(animalRepository.findAll()).thenReturn(fakeAnimalList());
        List<Animal> obtainedAnimal = animalService.getAnimal(animalName);
        verify(animalRepository, times(1)).findAll(); //The animal itself
        verify(animalRepository, times(2)).findById(any()); //Two calls, it's father and it's mother
    }

    @Test
    public void testGetAnimals() {
        animalService.getAnimals();
        verify(animalRepository, times(1)).findAll();
    }

    @Test
    public void validateFatherCreation(){
        //WIP
    }


}
