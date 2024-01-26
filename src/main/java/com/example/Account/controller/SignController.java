package com.example.Account.controller;

import com.example.Account.common.error.ErrorCode;
import com.example.Account.common.exception.ApiException;
import com.example.Account.model.SignInResultDto;
import com.example.Account.model.SignUpResultDto;
import com.example.Account.service.SignService;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sign-api")
public class SignController {

    @Autowired
    private final SignService signService;

    @PostMapping(value = "/sign-in")
    public SignInResultDto signIn(
        @ApiParam(value = "ID",required = true)@RequestParam String id,
        @ApiParam(value = "Password",required = true)@RequestParam String password
    )throws RuntimeException {

        SignInResultDto signInResultDto = signService.signIn(id,password);

        if (signInResultDto.getCode() == 0){
            return signInResultDto;
        }else{
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }
    }

    @PostMapping(value = "/sign-up")
    public SignUpResultDto signUp(
        @ApiParam(value = "ID",required = true)@RequestParam String id,
        @ApiParam(value = "비밀번호",required = true)@RequestParam String password,
        @ApiParam(value = "이름",required = true)@RequestParam String name,
        @ApiParam(value = "역할",required = true)@RequestParam String role
    ){
        SignUpResultDto signUpResultDto = signService.signUp(id,password,name,role);

        return signUpResultDto;
    }

}
