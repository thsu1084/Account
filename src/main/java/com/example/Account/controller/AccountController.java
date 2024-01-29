package com.example.Account.controller;

import com.example.Account.model.AccountDto;
import com.example.Account.model.CreateAccountDto;
import com.example.Account.model.DeleteAccountDto;
import com.example.Account.service.impl.AccountServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api")
public class AccountController {

    @Autowired
    private final AccountServiceImpl accountServiceImpl;


    @PostMapping("/create")
    public CreateAccountDto createAccount(
         @RequestHeader(name = "Authorization") String token,
         @RequestParam Long id,
         @RequestParam Long initialBalance
    ){
        return accountServiceImpl.createAccount(token,id, initialBalance);
    }


    @DeleteMapping("/delete")
    public DeleteAccountDto deleteAccount(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam Long id,
        @RequestParam String accountNumber
    ){
        return accountServiceImpl.deleteAccount(token,id, accountNumber);
    }

    @GetMapping("/account")
    public List<AccountDto> GetAllAccountById(
        @RequestParam Long id
    ){
        return accountServiceImpl.getAccount(id);
    }
}
