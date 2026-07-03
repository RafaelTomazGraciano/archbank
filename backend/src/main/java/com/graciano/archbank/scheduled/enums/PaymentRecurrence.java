package com.graciano.archbank.scheduled.enums;

import lombok.Getter;

@Getter
public enum PaymentRecurrence {

    NONE("NONE"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY");

    private final String value;

    PaymentRecurrence(String value){
        this.value = value;
    }

}
