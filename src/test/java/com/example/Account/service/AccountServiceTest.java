package com.example.Account.service;


import com.example.Account.common.enums.AccountStatus;
import com.example.Account.config.security.JwtTokenProvider;
import com.example.Account.db.Account;
import com.example.Account.db.User;
import com.example.Account.db.repository.AccountRepository;
import com.example.Account.db.repository.UserRepository;
import com.example.Account.model.CreateAccountDto;
import com.example.Account.model.DeleteAccountDto;
import com.example.Account.service.impl.AccountServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private final String token = "Bearer your_mocked_token";
    private final Long userId = 1L;
    private final Long initialBalance = 1000L;
    private final String accountNumber = "123456";

    @Test
    void successCreateAccount() {

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUid("userUid");
        mockUser.setName("aaa");
        mockUser.setPassword("1234");
        mockUser.setRole("ROLE_USER");

        Account mockAccount = new Account();

        mockAccount.setAccountNumber("123456");
        mockAccount.setUser(mockUser);
        mockAccount.setAccountStatus(AccountStatus.IN_USE);
        mockAccount.setBalance(0L);

        when(jwtTokenProvider.getUsername(any(String.class))).thenReturn("userUid");
        when(userRepository.findByUid(any(String.class))).thenReturn(Optional.of(mockUser));
        when(accountRepository.countByUser(any(User.class))).thenReturn(0);
        when(accountRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(mockAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateAccountDto result = accountService.createAccount(token, userId, initialBalance);

        assertNotNull(result);
        assertEquals("123457", result.getAccountNumber());
        assertNotNull(result.getRegisteredAt());
    }

    @Test
    void successDeleteAccount() {

        User mockUser = new User();
        mockUser.setId(userId);

        Account mockAccount = new Account();
        mockAccount.setAccountNumber(accountNumber);
        mockAccount.setUser(mockUser);
        mockAccount.setAccountStatus(AccountStatus.IN_USE);
        mockAccount.setBalance(0L);

        when(jwtTokenProvider.getUsername(any(String.class))).thenReturn("userUid");
        when(userRepository.findByUid(anyString())).thenReturn(Optional.of(mockUser));
        when(accountRepository.findByAccountNumber(eq(accountNumber))).thenReturn(Optional.of(mockAccount));

        DeleteAccountDto result = accountService.deleteAccount(token, userId, accountNumber);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(accountNumber, result.getAccountNumber());
        assertNotNull(result.getUnregisteredAt());
    }
}
