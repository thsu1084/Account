package com.example.Account.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum CommonResponse {

    SUCCESS(0,"Success"),FAIL(-1,"Fail");

    int code;
    String msg;
}
