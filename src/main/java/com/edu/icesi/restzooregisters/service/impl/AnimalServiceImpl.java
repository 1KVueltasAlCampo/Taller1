package com.edu.icesi.restzooregisters.service.impl;

import com.edu.icesi.restzooregisters.error.exception.AnimalError;
import com.edu.icesi.restzooregisters.error.exception.AnimalException;
import com.edu.icesi.restzooregisters.model.Animal;
import com.edu.icesi.restzooregisters.repository.AnimalRepository;
import com.edu.icesi.restzooregisters.service.AnimalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.edu.icesi.restzooregisters.constants.AnimalErrorCode.*;
import static com.edu.icesi.restzooregisters.constants.AnimalErrorCode.CODE_01;
import static com.edu.icesi.restzooregisters.constants.GenericTurtles.*;

@AllArgsConstructor
@Service
public class AnimalServiceImpl implements AnimalService {

    public final AnimalRepository animalRepository;

    @Override
    public List<Animal> getAnimal(String animalName) {
        Animal obtainedAnimal = getAnimalByName(animalName);
        if(obtainedAnimal != null){
            return getAnimalsList(obtainedAnimal);
        }
        throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_01,CODE_01.getMessage()));
    }

    private List<Animal> getAnimalsList(Animal obtainedAnimal){
        List<Animal> animals = new ArrayList<>();
        animals.add(obtainedAnimal);
        animals.add(getAnimalById(obtainedAnimal.getFatherID(),false));
        animals.add(getAnimalById(obtainedAnimal.getMotherID(),true));
        return animals;
    }

    private Animal getAnimalById(UUID id,boolean sex){ //False for male. True for female
        Animal generic;
        if(sex){
            generic = GENERIC_FEMALE_ANIMAL;
        }
        else{
            generic = GENERIC_MALE_ANIMAL;
        }
        return animalRepository.findById(id).orElse(generic);
    }

    private Animal getAnimalByName(String animalName) {
        return getAnimals().stream().peek(System.out::println).
                filter(animal -> animal.getName().equalsIgnoreCase(animalName)).findFirst().orElse(null);
    }

    @Override
    public Animal createAnimal(Animal animal) {
        animalCreationValidations(animal);
        return animalRepository.save(animal);
    }

    private void animalCreationValidations(Animal animal){
        isRepeated(animal.getName());
        validateParentsCreation(animal);
    }

    @Override
    public List<Animal> getAnimals() {
        return StreamSupport.stream(animalRepository.findAll().spliterator(),false).collect(Collectors.toList());
    }

    @Override
    public Animal updateAnimal(String animalName,Animal updatedAnimal) {
        Animal originalAnimal = getAnimalByName(animalName);
        if(originalAnimal != null){
             animalUpdateValidations(originalAnimal,updatedAnimal);
            return animalRepository.save(updatedAnimal);
        }
        throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_01,CODE_01.getMessage()));
    }

    private void animalUpdateValidations(Animal originalAnimal,Animal updatedAnimal){
        verificateNotNullAttributes(originalAnimal,updatedAnimal);
        validateParentsCreation(updatedAnimal);
        updatedAnimal.setId(originalAnimal.getId()); //To preserve the original ID
    }

    private void validateParentsCreation(Animal animal){ //False for male. True for female
        validateFatherCreation(animal);
        validateMotherCreation(animal);
    }

    private void validateFatherCreation(Animal animal){
        UUID fatherID = animal.getFatherID();
        if(fatherID != null){
            if (fatherExists(fatherID)) {
                animal.setFatherID(fatherID);
            }
        }
         else {
            animal.setFatherID(GENERIC_MALE_ID);
        }
    }
    private void validateMotherCreation(Animal animal){
        UUID motherID = animal.getMotherID();
        if(motherID != null){
            if (motherExists(motherID)) {
                animal.setMotherID(motherID);
            }
        }
        else {
            animal.setMotherID(GENERIC_FEMALE_ID);
        }
    }

    private boolean motherExists(UUID id){
        boolean female = true;
        Animal animal = getAnimalById(id,female);
        if( !(animal.getId().equals(GENERIC_FEMALE_ID)) ){
            if(animal.getSex()!='F'){
                throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_08,CODE_08.getMessage()));
            }
        }
        return true;
    }

    private boolean fatherExists(UUID id){
        boolean male = false;
        Animal animal = getAnimalById(id,male);
        if( !(animal.getId().equals(GENERIC_MALE_ID)) ){
            if(animal.getSex()!='M'){
                throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_08,CODE_08.getMessage()));
            }
        }
        return true;
    }


    private boolean isRepeated(String name){
        List<Animal> animals = getAnimals();
        for (Animal x : animals){
            if (x.getName().equals(name)){
                throw new AnimalException(HttpStatus.CONFLICT, new AnimalError(CODE_07,CODE_07.getMessage()));
            }
        }
        return false;
    }

    private void verificateNotNullAttributes(Animal originalAnimal, Animal newAnimal){
        if(newAnimal.getMotherID()==null){
            newAnimal.setMotherID(originalAnimal.getMotherID());
        }
        if(newAnimal.getFatherID()==null){
            newAnimal.setFatherID(originalAnimal.getFatherID());
        }
        if(newAnimal.getName()==null){
            newAnimal.setName(originalAnimal.getName());
        }
        if(newAnimal.getSex()==' '){
            newAnimal.setSex(originalAnimal.getSex());
        }
        if(newAnimal.getArrivalDate()==null){
            newAnimal.setArrivalDate(originalAnimal.getArrivalDate());
        }
    }
}
