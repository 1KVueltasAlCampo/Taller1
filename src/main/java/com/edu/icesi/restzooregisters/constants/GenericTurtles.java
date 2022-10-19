package com.edu.icesi.restzooregisters.constants;

import com.edu.icesi.restzooregisters.dto.AnimalDTO;
import com.edu.icesi.restzooregisters.model.Animal;

import java.time.LocalDateTime;
import java.util.UUID;

public class GenericTurtles {
    public final static UUID GENERIC_FEMALE_ID = UUID.nameUUIDFromBytes("femaleGenericId".getBytes());

    public final static UUID GENERIC_MALE_ID = UUID.nameUUIDFromBytes("maleGenericId".getBytes());
    public final static String TURTLE_NAME = "Not defined";
    public final static LocalDateTime TURTLE_DATE = LocalDateTime.of(2020,9,22,12,14,13);
    public final static AnimalDTO GENERIC_MALE_ANIMAL_DTO = new AnimalDTO(GENERIC_MALE_ID,TURTLE_NAME,'M',0,0,0,TURTLE_DATE,GENERIC_MALE_ID,GENERIC_FEMALE_ID);
    public final static Animal GENERIC_MALE_ANIMAL = new Animal(GENERIC_MALE_ID,TURTLE_NAME,'M',0,0,0,TURTLE_DATE,GENERIC_MALE_ID,GENERIC_FEMALE_ID);
    public final static AnimalDTO GENERIC_FEMALE_ANIMAL_DTO = new AnimalDTO(GENERIC_FEMALE_ID,TURTLE_NAME,'F',0,0,0,TURTLE_DATE,GENERIC_MALE_ID,GENERIC_FEMALE_ID);
    public final static Animal GENERIC_FEMALE_ANIMAL = new Animal(GENERIC_FEMALE_ID,TURTLE_NAME,'F',0,0,0,TURTLE_DATE,GENERIC_MALE_ID,GENERIC_FEMALE_ID);
}
