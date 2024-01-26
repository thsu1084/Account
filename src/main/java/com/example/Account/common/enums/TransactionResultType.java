package com.example.Account.common.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TransactionResultType {

    SUCCESS("성공"),
    FAIL("실패")
    ;

    private final String description;
}
