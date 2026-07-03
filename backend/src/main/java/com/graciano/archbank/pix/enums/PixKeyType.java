package com.graciano.archbank.pix.enums;

import lombok.Getter;

@Getter
public enum PixKeyType {

    CPF("CPF"),
    EMAIL("EMAIL"),
    PHONE("PHONE"),
    RANDOM("RANDOM");

    private final String value;

    PixKeyType(String value){
        this.value = value;
    }

}
