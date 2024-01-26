package com.example.Account.model;

import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignUpResultDto {

    private boolean success;

    private int code;

    private String msg;
}