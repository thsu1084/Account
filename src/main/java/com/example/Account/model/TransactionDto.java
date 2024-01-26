package com.example.Account.model;

import com.example.Account.common.enums.TransactionResultType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TransactionDto {

    private String accountNumber; // 계좌 번호

    private TransactionResultType transactionResultType;

    private String transactionId; // 거래 아이디

    private Long amount; // 거래 금액

    private LocalDateTime transactedAt; // 거래 일시
}
