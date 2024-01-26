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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api")
public class AccountController {

    @Autowired
    private final AccountServiceImpl accountServiceImpl;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/create")
    public CreateAccountDto createAccount(
         @RequestParam Long id,
         @RequestParam Long initialBalance
    ){
        return accountServiceImpl.createAccount(id, initialBalance);
    }

    @ApiImplicitParams({
        @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/delete")
    public DeleteAccountDto deleteAccount(
        @RequestParam Long id,
        @RequestParam String accountNumber
    ){
        return accountServiceImpl.deleteAccount(id, accountNumber);
    }

    @GetMapping("/account")
    public List<AccountDto> GetAllAccountById(
        @RequestParam Long id
    ){
        return accountServiceImpl.getAccount(id);
    }
}
