package com.edu.icesi.restzooregisters.service;

import com.edu.icesi.restzooregisters.dto.AnimalDTO;
import com.edu.icesi.restzooregisters.model.Animal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface AnimalService {
    public List<Animal> getAnimal(@PathVariable String animalName );
    public Animal createAnimal(@RequestBody Animal animal);
    public List<Animal> getAnimals();
    public Animal updateAnimal(String animalName,Animal animal);
}
