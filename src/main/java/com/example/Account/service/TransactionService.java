package com.example.Account.service;


import com.example.Account.db.Transaction;
import com.example.Account.model.TransactionDto;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

public interface TransactionService {

    TransactionDto useBalance(String token,Long id, String accountNumber, Long amount);

    TransactionDto FailToTransaction(String accountNumber , Long amount);

    TransactionDto cancelBalance(String token,String transactionId,String accountNumber, Long amount);

     TransactionDto getTransaction(String transactionId);
}
