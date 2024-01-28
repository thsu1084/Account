package com.example.Account.controller;


import static com.example.Account.common.enums.TransactionResultType.FAIL;


import com.example.Account.common.error.ErrorCode;
import com.example.Account.common.exception.ApiException;
import com.example.Account.model.TransactionDto;
import com.example.Account.service.impl.TransactionServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.concurrent.TimeUnit;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Transaction-api")
@Slf4j
public class TransactionController {

    @Autowired
    private final TransactionServiceImpl transactionServiceImpl;

    @Autowired
    private final RedissonClient redissonClient;

    public TransactionController(TransactionServiceImpl transactionServiceImpl,
        RedissonClient redissonClient) {
        this.transactionServiceImpl = transactionServiceImpl;
        this.redissonClient = redissonClient;
    }

    @ApiImplicitParams({
        @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @Transactional
    @PostMapping("/use")
    public TransactionDto useBalance(
        @RequestParam Long id,
        @RequestParam String accountNumber,
        @RequestParam Long amount
    ){

        RLock lock = redissonClient.getLock(accountNumber);

        TransactionDto transactionDto = null;

        boolean isLocked = false;

        try {

            log.info(Thread.currentThread()+" useBalance 의 Lock 획득 시도");
            isLocked = lock.tryLock(1, 20, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new ApiException(ErrorCode.SERVER_ERROR);
            }

            transactionDto = transactionServiceImpl.useBalance(id,accountNumber,amount);

            log.info(Thread.currentThread()+" 의 useBalance 로직 실행 완료");


        }catch (Exception e){
            log.info("에러 발생..");
        }finally {
            if (lock.isHeldByCurrentThread() && isLocked) { 
                log.info(Thread.currentThread() + " useBalance Lock 반납");
                lock.unlock();
            }
        }

        if (transactionDto == null||transactionDto.getTransactionResultType().equals(FAIL)){
            throw new ApiException(ErrorCode.BAD_REQUEST);
        }else{
            return transactionDto;
        }

       

    }


    @ApiImplicitParams({
        @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @Transactional
    @PostMapping("/cancel")
    public TransactionDto cancelBalance(
        @RequestParam String transactionId,
        @RequestParam String accountNumber,
        @RequestParam Long amount
    ){


        RLock lock = redissonClient.getLock(accountNumber);

        TransactionDto transactionDto = null;

        boolean isLocked = false;

        try {

            log.info(Thread.currentThread()+" cancelBalance 의 Lock 획득 시도");

            isLocked = lock.tryLock(1,20,TimeUnit.SECONDS);

            if (!isLocked){
                throw new ApiException(ErrorCode.SERVER_ERROR);
            }

            transactionDto = transactionServiceImpl.cancelBalance(transactionId,accountNumber,amount);

            log.info(Thread.currentThread()+" 의 cancelBalance로직 실행 완료");
        }catch (Exception e){
            log.info("에러 발생..");
        }finally {
            if (lock.isHeldByCurrentThread() && isLocked) { 
                log.info(Thread.currentThread() + " cancelBalance Lock 반납");
                lock.unlock();
            }
        }

        return transactionDto;

    }

    @GetMapping("/Transaction")
    public TransactionDto getTransaction(
        @RequestParam String transactionId
    ){
        return transactionServiceImpl.getTransaction(transactionId);
    }
}
