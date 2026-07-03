package com.graciano.archbank.notification.enums;

import lombok.Getter;

@Getter
public enum NotificationType {

    TRANSFER_RECEIVED("TRANSFER_RECEIVED"),
    PAYMENT_PROCESSED("PAYMENT_PROCESSED"),
    PAYMENT_FAILED("PAYMENT_FAILED"),
    LOGIN_ALERT("LOGIN_ALERT");

    private final String value;

    NotificationType(String value){
        this.value = value;
    }

}