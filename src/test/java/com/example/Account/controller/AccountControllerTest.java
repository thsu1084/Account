package com.example.Account.controller;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.Account.config.security.JwtTokenProvider;
import com.example.Account.db.repository.AccountRepository;
import com.example.Account.db.repository.UserRepository;
import com.example.Account.model.AccountDto;
import com.example.Account.model.CreateAccountDto;
import com.example.Account.model.DeleteAccountDto;
import com.example.Account.service.impl.AccountServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
public class AccountControllerTest {


    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountServiceImpl accountServiceImpl;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void successGetAllAccountById() throws Exception{

        List<AccountDto> accountDtoList = new ArrayList<>();

        accountDtoList.add(new AccountDto("6000000", 12100000L));

        given(accountServiceImpl.getAccount(anyLong()))
            .willReturn(accountDtoList);

        mockMvc.perform(get("/user-api/account")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("id","2")
            )
            .andDo(print())
            .andExpect(jsonPath("$[0].balance").value(12100000L))
            .andExpect(jsonPath("$[0].accountNumber").value("6000000"));



    }



    @Test
    void successCreateAccount() throws Exception {


        String token = "your_mocked_token";
        Long id = 1L;
        Long initialBalance = 1000L;
        CreateAccountDto expectedDto = new CreateAccountDto(1L, "1234567890", LocalDateTime.now());

        when(accountServiceImpl.createAccount(eq(token), eq(id), eq(initialBalance)))
                .thenReturn(expectedDto);

        mockMvc.perform(post("/user-api/create")
                        .header("Authorization", token)
                        .param("id", id.toString())
                        .param("initialBalance", initialBalance.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.accountNumber").value(expectedDto.getAccountNumber()))
                .andExpect(jsonPath("$.registeredAt").isNotEmpty());

        verify(accountServiceImpl, times(1)).createAccount(eq(token), eq(id), eq(initialBalance));
    }


    @Test
    void successDeleteAccount() throws Exception {

        String token = "your_mocked_token";
        Long id = 1L;
        String accountNumber = "1234567890";
        DeleteAccountDto expectedDto = new DeleteAccountDto(1L, accountNumber, LocalDateTime.now());

        when(accountServiceImpl.deleteAccount(eq(token), eq(id), eq(accountNumber)))
                .thenReturn(expectedDto);

        mockMvc.perform(delete("/user-api/delete")
                        .header("Authorization", token)
                        .param("id", id.toString())
                        .param("accountNumber", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.accountNumber").value(expectedDto.getAccountNumber()));

        verify(accountServiceImpl, times(1)).deleteAccount(eq(token), eq(id), eq(accountNumber));
    }

}
