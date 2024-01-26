package com.example.Account.service;

import com.example.Account.model.SignInResultDto;
import com.example.Account.model.SignUpResultDto;

public interface SignService {

    SignUpResultDto signUp(String id, String password, String name,String role);

    SignInResultDto signIn(String id , String password) throws RuntimeException;
}
