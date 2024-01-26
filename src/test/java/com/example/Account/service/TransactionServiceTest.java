package com.example.Account.service;

import com.example.Account.common.enums.AccountStatus;
import com.example.Account.common.enums.TransactionResultType;
import com.example.Account.common.enums.TransactionType;
import com.example.Account.db.Account;
import com.example.Account.db.Transaction;
import com.example.Account.db.User;
import com.example.Account.db.repository.AccountRepository;
import com.example.Account.db.repository.TransactionRepository;
import com.example.Account.db.repository.UserRepository;
import com.example.Account.model.TransactionDto;
import com.example.Account.service.impl.TransactionServiceImpl;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void successUseBalance() {

        Long userId = 1L;
        String accountNumber = "123456";
        Long amount = 100L;

        User user = new User();
        user.setId(userId);

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(200L);
        account.setAccountStatus(AccountStatus.IN_USE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));


        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(transactionCaptor.capture())).thenReturn(new Transaction());


        TransactionDto result = transactionService.useBalance(userId, accountNumber, amount);


        Transaction capturedTransaction = transactionCaptor.getValue();
        assertNotNull(capturedTransaction);
        assertEquals(TransactionType.USE, capturedTransaction.getTransactionType());
        assertEquals(TransactionResultType.SUCCESS, capturedTransaction.getTransactionResultType());
        assertEquals(account, capturedTransaction.getAccount());
        assertEquals(amount, capturedTransaction.getAmount());
        assertNotNull(capturedTransaction.getTransactedAt());

    }


    @Test
    public void successCancelBalance() {

        String transactionId = UUID.randomUUID().toString();
        String accountNumber = "123456";
        Long amount = 50L;

        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setTransactionType(TransactionType.USE);
        transaction.setTransactionResultType(TransactionResultType.SUCCESS);
        transaction.setAccount(new Account());
        transaction.setAmount(amount);
        transaction.setTransactedAt(LocalDateTime.now());

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(100L);

        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(transaction));
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        when(accountRepository.save(accountCaptor.capture())).thenReturn(account);
        when(transactionRepository.save(transactionCaptor.capture())).thenReturn(transaction);

        TransactionDto result = transactionService.cancelBalance(transactionId, accountNumber, amount);

        Account capturedAccount = accountCaptor.getValue();
        assertNotNull(capturedAccount);
        assertEquals(accountNumber, capturedAccount.getAccountNumber());
        assertEquals(150L, capturedAccount.getBalance()); // Check the updated balance

        Transaction capturedTransaction = transactionCaptor.getValue();
        assertNotNull(capturedTransaction);
        assertEquals(TransactionType.USE_CANCELLED, capturedTransaction.getTransactionType());
        assertEquals(TransactionResultType.SUCCESS, capturedTransaction.getTransactionResultType());
        assertEquals(amount, capturedTransaction.getAmount());
        assertNotNull(capturedTransaction.getTransactedAt());

        assertNotNull(result);
        assertEquals(accountNumber, result.getAccountNumber());
        assertEquals(TransactionResultType.SUCCESS, result.getTransactionResultType());
        assertEquals(transactionId, result.getTransactionId());
        assertNotNull(result.getTransactedAt());
        assertEquals(150L, result.getAmount());
    }

    @Test
    public void successGetTransaction() {

        String transactionId = UUID.randomUUID().toString();

        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setTransactionResultType(TransactionResultType.SUCCESS);
        transaction.setAmount(100L);
        transaction.setTransactedAt(LocalDateTime.now());
        transaction.setAccount(new Account());

        Account account = new Account();
        account.setAccountNumber("123456");

        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(transaction));
        when(accountRepository.findById(transaction.getAccount().getId())).thenReturn(Optional.of(account));


        TransactionDto result = transactionService.getTransaction(transactionId);

        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        assertEquals(100L, result.getAmount());
        assertNotNull(result.getTransactedAt());
        assertEquals(TransactionResultType.SUCCESS, result.getTransactionResultType());
        assertEquals("123456", result.getAccountNumber());

    }
}