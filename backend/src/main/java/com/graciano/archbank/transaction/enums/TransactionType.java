package com.graciano.archbank.transaction.enums;

import lombok.Getter;

@Getter
public enum TransactionType {

    PIX("PIX"),
    TRANSFER("TRANSFER"),
    DEPOSIT("DEPOSIT"),
    WITHDRAWAL("WITHDRAWAL");

    private final String value;

    TransactionType(String value){
        this.value = value;
    }

}
