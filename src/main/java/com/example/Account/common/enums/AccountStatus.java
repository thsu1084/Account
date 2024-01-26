package com.example.Account.common.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AccountStatus {

    IN_USE("사용중"),

    UNREGISTERED("해지")
    ;

    private final String description;
}
