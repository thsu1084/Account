package com.example.Account.controller;


import com.example.Account.common.enums.TransactionResultType;
import com.example.Account.common.exception.ApiException;
import com.example.Account.model.TransactionDto;
import com.example.Account.service.TransactionService;
import com.example.Account.service.impl.TransactionServiceImpl;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private TransactionServiceImpl transactionServiceImpl;

    @Mock
    private TransactionService transactionService;

    @Mock
    private RLock lock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        transactionController = new TransactionController(transactionServiceImpl ,redissonClient);
    }


    @Test
    public void successUseBalance() throws InterruptedException {

        Long id = 1L;
        String accountNumber = "123";
        Long amount = 100L;
        TransactionDto expectedDto = mock(TransactionDto.class);

        expectedDto = TransactionDto.builder()
            .transactionResultType(TransactionResultType.SUCCESS)
            .build() ;

        when(redissonClient.getLock(accountNumber)).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(transactionServiceImpl.useBalance(id, accountNumber, amount)).thenReturn(expectedDto);

        TransactionDto result = transactionController.useBalance(id, accountNumber, amount);

        assertNotNull(result);
        assertEquals(TransactionResultType.SUCCESS, result.getTransactionResultType());
    }



    @Test
    public void successCancelBalance() throws Exception {

        Long id = 1L;
        String accountNumber = "123";
        Long amount = 100L;
        String transactionId = "456";

        TransactionDto expectedDto = mock(TransactionDto.class);

        expectedDto = TransactionDto.builder()
            .transactedAt(LocalDateTime.now())
            .transactionId(transactionId)
            .amount(amount)
            .accountNumber(accountNumber)
            .transactionResultType(TransactionResultType.SUCCESS)
            .build() ;

        when(redissonClient.getLock(accountNumber)).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(transactionServiceImpl.cancelBalance(transactionId, accountNumber, amount)).thenReturn(expectedDto);

        TransactionDto result = transactionController.cancelBalance(transactionId, accountNumber, amount);

        assertNotNull(result);

    }




}