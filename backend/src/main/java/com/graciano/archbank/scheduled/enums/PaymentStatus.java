package com.graciano.archbank.scheduled.enums;


public enum PaymentStatus {

    PENDING("PENDING"),
    PROCESSED("PROCESSED"),
    FAILED("FAILED"),
    CANCELLED("CANCELLED");

    private final String value;

    PaymentStatus(String value){
        this.value = value;
    }

}
