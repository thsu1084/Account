package com.example.Account.service.impl;

import static com.example.Account.common.enums.AccountStatus.IN_USE;
import static com.example.Account.common.enums.AccountStatus.UNREGISTERED;

import com.example.Account.common.error.ErrorCode;
import com.example.Account.common.exception.ApiException;
import com.example.Account.db.Account;
import com.example.Account.db.User;
import com.example.Account.db.repository.AccountRepository;
import com.example.Account.db.repository.UserRepository;
import com.example.Account.model.AccountDto;
import com.example.Account.model.CreateAccountDto;
import com.example.Account.model.DeleteAccountDto;
import com.example.Account.service.AccountService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final AccountRepository accountRepository;


    private final Logger LOGGER =  LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    public AccountServiceImpl(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public CreateAccountDto createAccount(Long id , Long initialBalance) {

        LOGGER.info("createAccount 수행. id : {}",id);

       User user = userRepository.findById(id)
           .orElseThrow(()->new ApiException(ErrorCode.USER_NOT_FOUND));

       Integer count = accountRepository.countByUser(user);

       if (count >= 10){
           throw new ApiException(ErrorCode.BAD_REQUEST);
       }

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
            .map(account -> (
               Integer.parseInt(account.getAccountNumber()) + 1 +""
            )).orElse("1000000000");

        Account account = accountRepository.save(Account.builder()
           .user(user)
           .accountNumber(newAccountNumber)
           .accountStatus(IN_USE)
           .balance(initialBalance)
           .registeredAt(LocalDateTime.now())
           .unregisteredAt(LocalDateTime.from(LocalDateTime.now()).plusMonths(24))
           .build());

       return CreateAccountDto.builder()
           .id(account.getId())
           .accountNumber(account.getAccountNumber())
           .registeredAt(LocalDateTime.now())
           .build();
    }

    @Override
    public List<AccountDto> getAccount(Long id) {

        User user = userRepository.findById(id)
            .orElseThrow(()->new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Account> accountList = accountRepository.findAllByUser(user);

        List<AccountDto> accountDtoList = new ArrayList<>();

        for(Account account : accountList){
            accountDtoList.add(AccountDto.builder()
                    .accountNumber(account.getAccountNumber())
                    .balance(account.getBalance())
                    .build());
        }

        return accountDtoList;
    }


    @Override
    public DeleteAccountDto deleteAccount(Long id, String accountNumber) {


        User user = userRepository.findById(id).orElseThrow(()->new ApiException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()->new ApiException(ErrorCode.THE_ACCOUNT_DOES_NOT_EXIST));


        if (user.getId() != account.getUser().getId()){
            throw new ApiException(ErrorCode.THE_ACCOUNT_OWNER_IS_DIFFERENT);
        }

        if( account.getAccountStatus().equals(UNREGISTERED) ){
            throw new ApiException(ErrorCode.THE_ACCOUNT_IS_ALREADY_TERMINATED);
        }

        if (account.getBalance() != 0){
            throw new ApiException(ErrorCode.THERE_IS_MONEY_REMAINING_IN_THE_ACCOUNT);
        }

        account.setAccountStatus(UNREGISTERED);
        account.setUnregisteredAt(LocalDateTime.now());

        accountRepository.save(account);

        return DeleteAccountDto.builder()
            .id(user.getId())
            .accountNumber(account.getAccountNumber())
            .unregisteredAt(LocalDateTime.now())
            .build();
    }
}
