package com.edu.icesi.restzooregisters.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AnimalErrorCode {

    CODE_01("Animal not found"),
    CODE_02("Only letters and spaces are allowed in name. Limit of characters 120"),
    CODE_03("The arrival date cannot be later than the current date "),
    CODE_04("The height of the animal does not correspond to the physical characteristics of a turtle."),
    CODE_05("The weight of the animal does not correspond to the physical characteristics of a turtle."),
    CODE_06("The age of the animal does not correspond to a turtle."),
    CODE_07("There is already an animal with that name"),
    CODE_08("PARENT animal is not a male/female")
    ;

    private String message;

    public String getMessage(){
        return message;
    }
}