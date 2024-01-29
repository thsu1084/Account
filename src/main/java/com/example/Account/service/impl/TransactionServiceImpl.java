package com.example.Account.service.impl;


import static com.example.Account.common.enums.AccountStatus.UNREGISTERED;
import static com.example.Account.common.enums.TransactionResultType.FAIL;
import static com.example.Account.common.enums.TransactionResultType.SUCCESS;
import static com.example.Account.common.enums.TransactionType.USE;
import static com.example.Account.common.enums.TransactionType.USE_CANCELLED;



import com.example.Account.common.error.ErrorCode;
import com.example.Account.common.exception.ApiException;
import com.example.Account.config.security.JwtAuthenticationFilter;
import com.example.Account.config.security.JwtTokenProvider;
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
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public TransactionServiceImpl(UserRepository userRepository,AccountRepository accountRepository,
        TransactionRepository transactionRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public TransactionDto useBalance(String token,Long id, String accountNumber, Long amount) {

        log.info("token : {}",token);

        token = token.substring(JwtAuthenticationFilter.TOKEN_PREFIX.length());

        String uid = jwtTokenProvider.getUsername(token);

        log.info("uid : {}",uid);

        User user = userRepository.findByUid(uid)
            .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        log.info("user : {} , id {}",user,id);

        if (!Objects.equals(user.getId(),id)){
            log.info("사용자가 일치하지 않습니다.");
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(()->new ApiException(ErrorCode.THE_ACCOUNT_DOES_NOT_EXIST));

        if (account.getAccountStatus().equals(UNREGISTERED)){

            log.info("계좌가 해지상태 입니다.");

            return FailToTransaction(accountNumber,amount);

        }

        if (account.getBalance() < amount){

            log.info("잔액이 부족합니다.");
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
    public TransactionDto cancelBalance(String token,String transactionId, String accountNumber, Long amount)
    throws RuntimeException{

        log.info("token : {}",token);

        token = token.substring(JwtAuthenticationFilter.TOKEN_PREFIX.length());

        String uid = jwtTokenProvider.getUsername(token);

        log.info("uid : {}",uid);

        User user = userRepository.findByUid(uid)
            .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(()->new ApiException(ErrorCode.THE_ACCOUNT_DOES_NOT_EXIST));

        log.info("User : {} , Account : {}",user,account);

        if (!Objects.equals(user.getId(),account.getUser().getId())){
            log.info("사용자가 일치하지 않습니다.");
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
            .orElseThrow(()->new ApiException(ErrorCode.BAD_REQUEST));

        log.info("Transaction : {}",transaction);

        if (transaction.getTransactionType().equals(USE_CANCELLED)){
            log.info("이미 취소된 거래");
            throw new ApiException(ErrorCode.TRANSACTION_HAS_ALREADY_BEEN_CANCELED);
        }

        if (transaction.getTransactionResultType().equals(FAIL)){
            log.info("실패한 거래");
            throw new ApiException(ErrorCode.TRANSACTION_HAS_FAILED);
        }

        if (!Objects.equals(transaction.getAccount().getId(),account.getId())){
            log.info("거래 계좌 아이디와 계좌 아이디가 다를 때");
            throw new ApiException(ErrorCode.TRANSACTION_ACCOUNT_ID_DOES_NOT_MATCH);
        }

        log.info("transaction : {} {}",transaction.getAmount(),amount);


        if (!Objects.equals(transaction.getAmount(),amount)){
            log.info("거래 금액과 사용자가 요구하는 금액이 다를 때");
            throw new ApiException(ErrorCode.AMOUNT_DOES_NOT_MATCH);
        }


        if( transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))){
            log.info("1년이 지난 거래일 때");
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
