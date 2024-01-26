package com.example.Account.service;

import com.example.Account.db.Account;
import com.example.Account.db.User;
import com.example.Account.db.repository.AccountRepository;
import com.example.Account.db.repository.UserRepository;
import com.example.Account.model.AccountDto;
import com.example.Account.model.CreateAccountDto;
import com.example.Account.model.DeleteAccountDto;
import com.example.Account.service.impl.AccountServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.Account.common.enums.AccountStatus.IN_USE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testAccount = new Account();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

    }

    @Test
    public void successCreateAccount() {


        User mockUser = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(accountRepository.countByUser(any(User.class))).thenReturn(5);

        Account mockAccount = new Account();
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);


        CreateAccountDto result = accountService.createAccount(1L, 1000L);


        assertNotNull(result);
        assertEquals(mockAccount.getAccountNumber(), result.getAccountNumber());
    }

    @Test
    public void successGetAccount() {

        when(accountRepository.findAllByUser(any(User.class))).thenReturn(Collections.singletonList(testAccount));


        List<AccountDto> result = accountService.getAccount(1L);


        assertFalse(result.isEmpty());
    }

    @Test
    public void successDeleteAccount() {

        User mockUser = mock(User.class);
        Account mockAccount = mock(Account.class);

        Long userId = 1L;
        String accountNumber = "123456789";

        when(mockUser.getId()).thenReturn(userId);
        when(mockAccount.getUser()).thenReturn(mockUser);
        when(mockAccount.getAccountNumber()).thenReturn(accountNumber);
        when(mockAccount.getAccountStatus()).thenReturn(IN_USE);
        when(mockAccount.getBalance()).thenReturn(0L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(mockAccount));


        DeleteAccountDto result = accountService.deleteAccount(userId, accountNumber);


        assertNotNull(result);
        assertEquals(IN_USE, mockAccount.getAccountStatus());
    }


}