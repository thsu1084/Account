package com.example.Account.controller;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.Account.common.enums.TransactionResultType;
import com.example.Account.model.TransactionDto;
import com.example.Account.service.impl.TransactionServiceImpl;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
@WithMockUser
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionServiceImpl transactionServiceImpl;

    @MockBean
    private RedissonClient redissonClient;
    @MockBean
    private RLock lock ;

    @BeforeEach
    void setUp() throws InterruptedException {

        lock = mock(RLock.class);

        when(redissonClient.getLock(anyString())).thenReturn(lock);

        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);
    }

    @Test
    public void successUseBalance() throws Exception {

        String token = "Bearer your_mocked_token";
        Long id = 1L;
        String accountNumber = "1234567890";
        Long amount = 500L;
        TransactionDto expectedDto = new TransactionDto();
        expectedDto.setAccountNumber(accountNumber);
        expectedDto.setTransactionResultType(TransactionResultType.SUCCESS);
        expectedDto.setAmount(amount);
        expectedDto.setTransactedAt(LocalDateTime.now());

        given(transactionServiceImpl.useBalance(eq(token), eq(id), eq(accountNumber), eq(amount)))
            .willReturn(expectedDto);

        mockMvc.perform(post("/Transaction-api/use")
                .with(csrf())
                .header("Authorization", token)
                .param("id", id.toString())
                .param("accountNumber", accountNumber)
                .param("amount", amount.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountNumber").value(expectedDto.getAccountNumber()))
            .andExpect(jsonPath("$.transactionResultType").value(expectedDto.getTransactionResultType().toString()))
            .andExpect(jsonPath("$.amount").value(expectedDto.getAmount()))
            .andExpect(jsonPath("$.transactedAt").isNotEmpty());

        verify(transactionServiceImpl, times(1)).useBalance(eq(token), eq(id), eq(accountNumber), eq(amount));
    }

    @Test
    public void successCancelBalance() throws Exception{

        String token = "Bearer your_mocked_token";
        Long id = 1L;
        String transactionId = "aabbcc";
        String accountNumber = "1234567890";
        Long amount = 500L;
        TransactionDto expectedDto = new TransactionDto();
        expectedDto.setAccountNumber(accountNumber);
        expectedDto.setTransactionResultType(TransactionResultType.SUCCESS);
        expectedDto.setAmount(amount);
        expectedDto.setTransactedAt(LocalDateTime.now());

        given(transactionServiceImpl.cancelBalance(eq(token), eq(transactionId), eq(accountNumber), eq(amount)))
            .willReturn(expectedDto);

        mockMvc.perform(post("/Transaction-api/cancel")
                .with(csrf())
                .header("Authorization", token)
                .param("transactionId", transactionId)
                .param("accountNumber", accountNumber)
                .param("amount", amount.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountNumber").value(expectedDto.getAccountNumber()))
            .andExpect(jsonPath("$.transactionResultType").value(expectedDto.getTransactionResultType().toString()))
            .andExpect(jsonPath("$.amount").value(expectedDto.getAmount()))
            .andExpect(jsonPath("$.transactedAt").isNotEmpty());

        verify(transactionServiceImpl, times(1))
            .cancelBalance(eq(token), eq(transactionId), eq(accountNumber), eq(amount));
    }



}
