package com.example.Account.common.error;

import lombok.*;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Getter
public enum ErrorCode implements ErrorCodeIfs {

    OK(200 , 200 , "성공"),

    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), 400, "잘못된 요청"),

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 500, "서버에러"),

    NULL_POINT(HttpStatus.INTERNAL_SERVER_ERROR.value(), 512, "Null point"),

    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST.value(), 514,"잘못된 비밀번호"),

    REGISTRATION_FAILED(HttpStatus.NO_CONTENT.value(), 515,"회원 가입 실패"),

    LOGIN_FAILED(HttpStatus.NO_CONTENT.value(), 516,"로그인 실패"),

    USER_NOT_FOUND(HttpStatus.NO_CONTENT.value(), 517,"사용자를 찾을 수 없습니다"),

    THE_ACCOUNT_DOES_NOT_EXIST(HttpStatus.NO_CONTENT.value(), 518,"계좌가 존재하지 않습니다"),

    THE_ACCOUNT_OWNER_IS_DIFFERENT(HttpStatus.NO_CONTENT.value(), 519,"계좌의 소유주가 다릅니다"),

    THE_ACCOUNT_IS_ALREADY_TERMINATED(HttpStatus.NO_CONTENT.value(), 520,"계좌가 이미 해지 상태 입니다"),

    THERE_IS_MONEY_REMAINING_IN_THE_ACCOUNT(HttpStatus.NO_CONTENT.value(), 521,"계좌에 돈이 남아있습니다"),

    INSUFFICIENT_BALANCE(HttpStatus.NO_CONTENT.value(), 522,"잔액이 부족합니다"),

    TRANSACTION_HAS_ALREADY_BEEN_CANCELED(HttpStatus.NO_CONTENT.value(), 523,"이미 취소된 거래 입니다"),

    TRANSACTION_HAS_FAILED(HttpStatus.NO_CONTENT.value(),524,"실패한 거래 입니다"),

    TRANSACTION_ACCOUNT_ID_DOES_NOT_MATCH(HttpStatus.NO_CONTENT.value() ,525,"거래 계좌 아이디와 동일하지 않습니다"),

    AMOUNT_DOES_NOT_MATCH(HttpStatus.NO_CONTENT.value(), 526,"금액이 일치하지 않습니다"),

    TRANSACTION_IS_OLDER_THAN_1_YEAR(HttpStatus.NO_CONTENT.value(), 527,"1년이 지난 거래 입니다")

    ;


    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String description;

}