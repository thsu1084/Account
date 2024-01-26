package com.example.Account.common.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TransactionType {

    USE("사용"),
    USE_CANCELLED("사용취소")
    ;

    private final String description;
}
