package com.example.Account.service;

import com.example.Account.common.enums.AccountStatus;
import com.example.Account.common.enums.TransactionResultType;
import com.example.Account.common.enums.TransactionType;
import com.example.Account.config.security.JwtTokenProvider;
import com.example.Account.db.Account;
import com.example.Account.db.Transaction;
import com.example.Account.db.User;
import com.example.Account.db.repository.AccountRepository;
import com.example.Account.db.repository.TransactionRepository;
import com.example.Account.db.repository.UserRepository;
import com.example.Account.model.TransactionDto;
import com.example.Account.service.impl.TransactionServiceImpl;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class TransactionServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks 
    private TransactionServiceImpl transactionService;


    @Test
    public void successUseBalance() {
        String token = "your_token_here";
        Long id = 1L;
        String accountNumber = "123456789";
        Long amount = 1000L;
        String uid = "user_uid";

        User mockUser = new User();
        mockUser.setId(id);

        Account mockAccount = new Account();
        mockAccount.setAccountStatus(AccountStatus.IN_USE);
        mockAccount.setBalance(2000L);

        when(jwtTokenProvider.getUsername(anyString())).thenReturn(uid);
        when(userRepository.findByUid(eq(uid))).thenReturn(Optional.of(mockUser));
        when(accountRepository.findByAccountNumber(eq(accountNumber))).thenReturn(Optional.of(mockAccount));

        Transaction mockTransaction = Transaction.builder()
            .transactionId(UUID.randomUUID().toString().replaceAll("-", ""))
            .build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        TransactionDto result = transactionService.useBalance(token, id, accountNumber, amount);

        assertNotNull(result);
        assertEquals(TransactionResultType.SUCCESS, result.getTransactionResultType());
        assertEquals(accountNumber, result.getAccountNumber());
        assertNotNull(result.getTransactionId());
        assertEquals(amount, result.getAmount());
        assertNotNull(result.getTransactedAt());

        verify(jwtTokenProvider, times(1)).getUsername(anyString());
        verify(userRepository, times(1)).findByUid(eq(uid));
        verify(accountRepository, times(1)).findByAccountNumber(eq(accountNumber));
        verify(accountRepository, times(1)).save(eq(mockAccount));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void successCancelBalance(){

        String token = "your_token_here";
        String transactionId = "transaction_id";
        String accountNumber = "123456789";
        Long amount = 1000L;
        String uid = "user_uid";

        User mockUser = new User();
        mockUser.setId(1L);

        Account mockAccount = new Account();
        mockAccount.setUser(mockUser);
        mockAccount.setId(1L);
        mockAccount.setBalance(2000L);

        Transaction mockTransaction = Transaction.builder()
            .transactionId(transactionId)
            .transactionType(TransactionType.USE)
            .transactionResultType(TransactionResultType.SUCCESS)
            .account(mockAccount)
            .amount(amount)
            .transactedAt(LocalDateTime.now().minusMonths(6))
            .build();

        when(jwtTokenProvider.getUsername(anyString())).thenReturn(uid);
        when(userRepository.findByUid(eq(uid))).thenReturn(Optional.of(mockUser));
        when(accountRepository.findByAccountNumber(eq(accountNumber))).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.findByTransactionId(eq(transactionId))).thenReturn(Optional.of(mockTransaction));

        TransactionDto result = transactionService.cancelBalance(token, transactionId, accountNumber, amount);

        assertNotNull(result);
        assertEquals(TransactionResultType.SUCCESS, result.getTransactionResultType());
        assertEquals(accountNumber, result.getAccountNumber());
        assertEquals(transactionId, result.getTransactionId());
        assertNotNull(result.getTransactedAt());

        assertEquals(3000L, result.getAmount());

        verify(jwtTokenProvider, times(1)).getUsername(anyString());
        verify(userRepository, times(1)).findByUid(eq(uid));
        verify(accountRepository, times(1)).findByAccountNumber(eq(accountNumber));
        verify(accountRepository, times(1)).save(eq(mockAccount));
        verify(transactionRepository, times(1)).save(eq(mockTransaction));
    }



}
