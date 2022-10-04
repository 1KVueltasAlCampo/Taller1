package com.edu.icesi.restzooregisters.controller;

import com.edu.icesi.restzooregisters.api.RestZooRegistersAPI;
import com.edu.icesi.restzooregisters.constants.AnimalErrorCode;
import com.edu.icesi.restzooregisters.dto.AnimalDTO;
import com.edu.icesi.restzooregisters.error.exception.AnimalError;
import com.edu.icesi.restzooregisters.error.exception.AnimalException;
import com.edu.icesi.restzooregisters.mapper.AnimalMapper;
import com.edu.icesi.restzooregisters.service.AnimalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.edu.icesi.restzooregisters.constants.AnimalErrorCode.*;
import static com.edu.icesi.restzooregisters.constants.TurtleCharacteristics.*;

@RestController
@AllArgsConstructor
public class AnimalController implements RestZooRegistersAPI {

    public final AnimalService animalService;
    public final AnimalMapper animalMapper;

    @Override
    public List<AnimalDTO> getAnimal(String animalName) {
        return animalService.getAnimal(animalName).stream().map(animalMapper::fromAnimal).collect(Collectors.toList());
    }

    @Override
    public AnimalDTO createAnimal(AnimalDTO animalDTO) {
        allAnimalValidations(animalDTO);
        return animalMapper.fromAnimal(animalService.createAnimal(animalMapper.fromDTO(animalDTO)));
    }

    @Override
    public List<AnimalDTO> getAnimals() {
        return animalService.getAnimals().stream().map(animalMapper::fromAnimal).collect(Collectors.toList());
    }

    @Override
    public AnimalDTO updateAnimal(String animalName, AnimalDTO animalDTO) {
        allAnimalValidations(animalDTO);
        return animalMapper.fromAnimal(animalService.updateAnimal(animalName,animalMapper.fromDTO(animalDTO)));
    }

    private void allAnimalValidations(AnimalDTO animalDTO){
        validateName(animalDTO.getName());
        validateDate(animalDTO.getArrivalDate());
        validateAnimalHeight(animalDTO.getHeight());
        validateAnimalWeight(animalDTO.getWeight());
        validateAnimalAge(animalDTO.getAge());

    }

    private void validateName(String name){
        if(!name.matches("[aA-zZ ]{0,120}")){
            throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_02,CODE_02.getMessage()));
        }
    }

    private void validateDate(LocalDateTime date){
        if (LocalDateTime.now().isBefore(date)){
            throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_03,CODE_03.getMessage()));
        }
    }
    private void validateAnimalHeight(double animalHeight) {
        if (!(MIN_HEIGHT <= animalHeight && animalHeight <= MAX_HEIGHT)) {
            throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_04, CODE_04.getMessage()));
        }
    }

    private void validateAnimalWeight(double animalWeight) {
        if (!(MIN_WEIGHT <= animalWeight && animalWeight <= MAX_WEIGHT)) {
            throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_05, CODE_05.getMessage()));
        }
    }

    private void validateAnimalAge(int age){
        if(!(0<age && age<=MAX_AGE)){
            throw new AnimalException(HttpStatus.BAD_REQUEST, new AnimalError(CODE_06, CODE_06.getMessage()));
        }
    }



}
