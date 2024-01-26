package com.example.Account.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignInResultDto {

    private String token;

    private boolean success;

    private int code;

    private String msg;

}