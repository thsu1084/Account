package com.example.Account.service;


import com.example.Account.db.Account;
import com.example.Account.model.AccountDto;
import com.example.Account.model.CreateAccountDto;
import com.example.Account.model.DeleteAccountDto;
import java.util.List;

public interface AccountService {

    CreateAccountDto createAccount(Long id , Long initialBalance);

    List<AccountDto> getAccount(Long id);

    DeleteAccountDto deleteAccount(Long id, String accountNumber);
}
