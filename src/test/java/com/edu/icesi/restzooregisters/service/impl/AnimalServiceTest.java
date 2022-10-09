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
import java.util.Optional;
import java.util.UUID;

import static com.edu.icesi.restzooregisters.constants.GenericTurtles.*;
import static com.edu.icesi.restzooregisters.constants.GenericTurtles.GENERIC_FEMALE_ID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AnimalServiceTest {

    private AnimalRepository animalRepository;
    private AnimalService animalService;
    private String animalName;
    private Animal sonAnimal;
    private Animal updatedSonAnimal;
    private Animal motherAnimal;
    private Animal fatherAnimal;

    @BeforeEach
    private void init() {
        animalRepository = mock(AnimalRepository.class);
        animalService = new AnimalServiceImpl(animalRepository);
    }

    private void setupSceneSonAnimal(){
        animalName="Pablito";
        char sex='M';
        double weight=131;
        int age=13;
        double height=46;
        LocalDateTime arrivalDate= TURTLE_DATE;
        UUID fatherID=GENERIC_MALE_ID;
        UUID motherID=GENERIC_FEMALE_ID;
        sonAnimal = new Animal(UUID.randomUUID(),animalName,sex,weight,age,height,arrivalDate,fatherID,motherID);
    }

    private void setupSceneUpdatedSonAnimal(){
        String name = "PablitoSSJ";
        char sex='M';
        double weight=140;
        int age=14;
        double height=47;
        LocalDateTime arrivalDate= TURTLE_DATE;
        UUID fatherID=GENERIC_MALE_ID;
        UUID motherID=GENERIC_FEMALE_ID;
        updatedSonAnimal=new Animal(UUID.randomUUID(),name,sex,weight,age,height,arrivalDate,fatherID,motherID);
    }

    private void setupSceneMotherAnimal(){
        String name = "Camila";
        char sex='F';
        double weight=140;
        int age=17;
        double height=47;
        LocalDateTime arrivalDate= TURTLE_DATE;
        UUID fatherID=GENERIC_MALE_ID;
        UUID motherID=GENERIC_FEMALE_ID;
        motherAnimal=new Animal(UUID.randomUUID(),name,sex,weight,age,height,arrivalDate,fatherID,motherID);
    }

    private void setupSceneFatherAnimal(){
        String name = "Patricio";
        char sex='M';
        double weight=140;
        int age=17;
        double height=47;
        LocalDateTime arrivalDate= TURTLE_DATE;
        UUID fatherID=GENERIC_MALE_ID;
        UUID motherID=GENERIC_FEMALE_ID;
        fatherAnimal=new Animal(UUID.randomUUID(),name,sex,weight,age,height,arrivalDate,fatherID,motherID);
    }

    private List<Animal> fakeAnimalList(){
        List<Animal> fakeAnimals = new ArrayList<>();
        fakeAnimals.add(sonAnimal);
        fakeAnimals.add(fatherAnimal);
        fakeAnimals.add(motherAnimal);
        return fakeAnimals;
    }



    private boolean createGeneratesException(){
        try {
            animalService.createAnimal(sonAnimal);
        }
        catch (Exception e){
            return true;
        }
        return false;
    }

    private boolean updateGeneratesException(){
        when(animalRepository.findAll()).thenReturn(fakeAnimalList());
        try {
            animalService.updateAnimal(animalName,updatedSonAnimal);
        }
        catch (Exception e){
            return true;
        }
        return false;
    }

    @Test
    public void testCreateAnimal(){
        setupSceneSonAnimal();
        assertFalse(createGeneratesException());
        verify(animalRepository,times(1)).save(any());
    }

    @Test
    public void testUpdateAnimal(){
        setupSceneSonAnimal();
        setupSceneUpdatedSonAnimal();
        assertFalse(updateGeneratesException());
        verify(animalRepository,times(1)).save(any());
    }

    @Test
    public void testGetAnimal() {
        setupSceneSonAnimal();
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
    public void testFatherInCreate(){
        setupSceneSonAnimal();
        setupSceneMotherAnimal();
        sonAnimal.setFatherID(motherAnimal.getId());
        when(animalRepository.findById(sonAnimal.getFatherID())).thenReturn(Optional.ofNullable(motherAnimal));
        assertTrue(createGeneratesException());
    }

    @Test
    public void testFatherInUpdate(){
        setupSceneSonAnimal();
        setupSceneUpdatedSonAnimal();
        setupSceneMotherAnimal();
        updatedSonAnimal.setFatherID(motherAnimal.getId());
        when(animalRepository.findById(updatedSonAnimal.getFatherID())).thenReturn(Optional.ofNullable(motherAnimal));
        assertTrue(updateGeneratesException());
    }

    @Test
    public void testMotherInCreate(){
        setupSceneSonAnimal();
        setupSceneFatherAnimal();
        sonAnimal.setMotherID(fatherAnimal.getId());
        when(animalRepository.findById(sonAnimal.getMotherID())).thenReturn(Optional.ofNullable(fatherAnimal));
        assertTrue(createGeneratesException());
    }

    @Test
    public void testMotherInUpdate(){
        setupSceneSonAnimal();
        setupSceneUpdatedSonAnimal();
        setupSceneFatherAnimal();
        updatedSonAnimal.setMotherID(fatherAnimal.getId());
        when(animalRepository.findById(updatedSonAnimal.getMotherID())).thenReturn(Optional.ofNullable(fatherAnimal));
        assertTrue(updateGeneratesException());
    }

    @Test
    public void testValidateNoSexChange(){
        setupSceneSonAnimal();
        setupSceneUpdatedSonAnimal();
        updatedSonAnimal.setSex('F');
        assertTrue(updateGeneratesException());
    }

}
