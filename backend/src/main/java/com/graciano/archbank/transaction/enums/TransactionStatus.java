package com.graciano.archbank.transaction.enums;

public enum TransactionStatus {

    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    REVERSED("REVERSED");

    private final String value;

    TransactionStatus(String value){
        this.value = value;
    }

}
