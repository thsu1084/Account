package com.example.Account.service.impl;

import static com.example.Account.common.enums.AccountStatus.UNREGISTERED;
import static com.example.Account.common.enums.TransactionResultType.FAIL;
import static com.example.Account.common.enums.TransactionResultType.SUCCESS;
import static com.example.Account.common.enums.TransactionType.USE;
import static com.example.Account.common.enums.TransactionType.USE_CANCELLED;



import com.example.Account.common.error.ErrorCode;
import com.example.Account.common.exception.ApiException;
import com.example.Account.db.Account;
import com.example.Account.db.Transaction;
import com.example.Account.db.User;
import com.example.Account.db.repository.AccountRepository;
import com.example.Account.db.repository.TransactionRepository;
import com.example.Account.db.repository.UserRepository;
import com.example.Account.model.TransactionDto;
import com.example.Account.service.TransactionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(UserRepository userRepository,AccountRepository accountRepository,
        TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionDto useBalance(Long id, String accountNumber, Long amount) {

        User user = userRepository.findById(id)
            .orElseThrow(()->new ApiException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(()->new ApiException(ErrorCode.THE_ACCOUNT_DOES_NOT_EXIST));

        if (account.getAccountStatus().equals(UNREGISTERED)){

            return FailToTransaction(accountNumber,amount);    
        }

        if (account.getBalance() < amount){
            
            return FailToTransaction(accountNumber,amount);
        
        }



        account.setBalance(account.getBalance() - amount);

        accountRepository.save(account);


        Transaction transaction = transactionRepository.save(
            Transaction.builder()
                .transactedAt(LocalDateTime.now())
                .transactionType(USE)
                .transactionResultType(SUCCESS)
                .account(account)
                .amount(amount)
                .balanceSnapshot(account.getBalance())
                .transactionId(UUID.randomUUID().toString().replaceAll("-",""))
                .build()
        );


        return TransactionDto.builder()
            .accountNumber(accountNumber)
            .transactionResultType(SUCCESS)
            .transactionId(transaction.getTransactionId())
            .amount(amount)
            .transactedAt(LocalDateTime.now())
            .build();
    }


    @Override
    public TransactionDto FailToTransaction(String accountNumber, Long amount) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(()->new ApiException(ErrorCode.THE_ACCOUNT_DOES_NOT_EXIST));

        Transaction transaction = transactionRepository.save(Transaction.builder()
                .amount(amount)
                .account(account)
                .transactedAt(LocalDateTime.now())
                .transactionType(USE)
                .transactionResultType(FAIL)
                .transactionId(UUID.randomUUID().toString().replaceAll("-",""))
                .balanceSnapshot(account.getBalance())
                .build());


        return TransactionDto.builder()
            .accountNumber(accountNumber)
            .transactionResultType(FAIL)
            .transactionId(transaction.getTransactionId())
            .amount(amount)
            .transactedAt(LocalDateTime.now())
            .build();


    }



    @Override
    public TransactionDto cancelBalance(String transactionId, String accountNumber, Long amount)
    throws RuntimeException{

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
            .orElseThrow(()->new ApiException(ErrorCode.BAD_REQUEST));

        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(()->new ApiException(ErrorCode.THE_ACCOUNT_DOES_NOT_EXIST));

        // 이미 취소된 거래 일 떄
        if (transaction.getTransactionType().equals(USE_CANCELLED)){
            throw new ApiException(ErrorCode.TRANSACTION_HAS_ALREADY_BEEN_CANCELED);
        }

        // 실패한 거래 일 때
        if (transaction.getTransactionResultType().equals(FAIL)){
            throw new ApiException(ErrorCode.TRANSACTION_HAS_FAILED);
        }

        // 거래 계좌 아이디와 계좌 아이디가 다를 때
        if (!Objects.equals(transaction.getAccount().getId(),account.getId())){
            throw new ApiException(ErrorCode.TRANSACTION_ACCOUNT_ID_DOES_NOT_MATCH);
        }

        log.info("transaction : {} {}",transaction.getAmount(),amount);

        // 거래 금액과 사용자가 요구하는 금액이 다를 때
        if (!Objects.equals(transaction.getAmount(),amount)){
            throw new ApiException(ErrorCode.AMOUNT_DOES_NOT_MATCH);
        }

        // 1년이 지난 거래일 때
        if( transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))){
            throw new ApiException(ErrorCode.TRANSACTION_IS_OLDER_THAN_1_YEAR);
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        transaction.setTransactionType(USE_CANCELLED);
        transactionRepository.save(transaction);

        return TransactionDto.builder()
            .accountNumber(accountNumber)
            .transactionResultType(SUCCESS)
            .transactionId(transactionId)
            .transactedAt(LocalDateTime.now())
            .amount(account.getBalance())
            .build();
    }

    @Override
    public TransactionDto getTransaction(String transactionId) {

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
            .orElseThrow(()->new ApiException(ErrorCode.BAD_REQUEST));


        Account account = accountRepository.findById(transaction.getAccount().getId())
            .orElseThrow(()->new ApiException(ErrorCode.THE_ACCOUNT_DOES_NOT_EXIST));

        return TransactionDto.builder()
            .transactionId(transaction.getTransactionId())
            .amount(transaction.getAmount())
            .transactedAt(transaction.getTransactedAt())
            .transactionResultType(transaction.getTransactionResultType())
            .accountNumber(account.getAccountNumber())
            .build();
    }


}
