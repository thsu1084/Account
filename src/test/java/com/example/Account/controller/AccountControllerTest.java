package com.example.Account.controller;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.Account.model.AccountDto;
import com.example.Account.model.CreateAccountDto;
import com.example.Account.model.DeleteAccountDto;
import com.example.Account.service.impl.AccountServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
@WithMockUser
public class AccountControllerTest {

    @MockBean
    private AccountServiceImpl accountServiceImpl;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

        given(accountServiceImpl.createAccount(anyLong(),anyLong()))
            .willReturn(CreateAccountDto.builder()
                .id(1L)
                .accountNumber("100000001")
                .registeredAt(LocalDateTime.now())
                .build());


        mockMvc.perform(post("/user-api/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
            .param("id","2")
            .param("initialBalance","1234")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.accountNumber").value("100000001"))
            .andDo(print());

    }


    @Test
    void successDeleteAccount() throws Exception {

        given(accountServiceImpl.deleteAccount(anyLong(),anyString()))
            .willReturn(DeleteAccountDto.builder()
                .id(1L)
                .accountNumber("123456789")
                .unregisteredAt(LocalDateTime.now())
                .build());


        mockMvc.perform(delete("/user-api/delete")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("id","2")
                .param("accountNumber","1234")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.accountNumber").value("123456789"))
            .andDo(print());

    }

}
