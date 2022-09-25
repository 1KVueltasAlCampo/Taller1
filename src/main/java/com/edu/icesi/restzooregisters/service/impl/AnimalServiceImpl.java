package com.edu.icesi.restzooregisters.service.impl;

import com.edu.icesi.restzooregisters.dto.AnimalDTO;
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
        List<Animal> animals = new ArrayList<>();
        Animal animal = getAnimalByName(animalName);
        if(animal == null){
            throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_01,CODE_01.getMessage()));
        }
        animals.add(animal);
        animals.add(getAnimalById(animal.getFatherID(),false));
        animals.add(getAnimalById(animal.getMotherID(),true));
        return animals;
    }

    private Animal getAnimalById(UUID id,boolean sex){ //False for male. True for female
        if(sex){
            return animalRepository.findById(id).orElse(GENERIC_FEMALE_ANIMAL);
        }
        else{
            return animalRepository.findById(id).orElse(GENERIC_MALE_ANIMAL);
        }
    }

    private Animal getAnimalByName(String animalName) {
        return getAnimals().stream().peek(System.out::println).
                filter(animal -> animal.getName().equalsIgnoreCase(animalName)).findFirst().orElse(null);
    }

    @Override
    public Animal createAnimal(Animal animal) {
        isRepeated(animal.getName());
        validateCreation(animal);
        return animalRepository.save(animal);
    }

    @Override
    public List<Animal> getAnimals() {
        return StreamSupport.stream(animalRepository.findAll().spliterator(),false).collect(Collectors.toList());
    }

    @Override
    public Animal updateAnimal(Animal animal) {
        Animal searchedAnimal = getAnimalByName(animal.getName());
        if(searchedAnimal != null){
            verificateNotNull(searchedAnimal,animal);
            validateCreation(animal);
            animal.setId(searchedAnimal.getId());
            return animalRepository.save(animal);
        }
        throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_01,CODE_01.getMessage()));
    }

    private void validateCreation(Animal animal){ //False for male. True for female
        parentsExists(animal.getFatherID(),false);
        parentsExists(animal.getMotherID(),true);
    }

    private void parentsExists(UUID id,boolean sex){ //False for male. True for female
        if(id != null){
            Animal animal = getAnimalById(id,sex);
            if(sex){
                if(animal.equals(GENERIC_FEMALE_ANIMAL) || animal.getSex()!='F'){
                    throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_08,CODE_08.getMessage()));
                }
            } 
            else{
                if(animal.equals(GENERIC_MALE_ANIMAL)||animal.getSex()!='M'){
                    throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_08,CODE_08.getMessage()));
                }
            }
        }
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

    private void verificateNotNull(Animal searched,Animal newAnimal){
        if(newAnimal.getMotherID()==null){
            newAnimal.setMotherID(searched.getMotherID());
        }
        if(newAnimal.getFatherID()==null){
            newAnimal.setFatherID(searched.getFatherID());
        }
        if(newAnimal.getName()==null){
            newAnimal.setName(searched.getName());
        }
        if(newAnimal.getSex()==' '){
            newAnimal.setSex(searched.getSex());
        }
        if(newAnimal.getArrivalDate()==null){
            newAnimal.setArrivalDate(searched.getArrivalDate());
        }
    }
}
