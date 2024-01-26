package com.example.Account.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class CreateAccountDto {

        private Long id;

        private String accountNumber;

        private LocalDateTime registeredAt;

}
