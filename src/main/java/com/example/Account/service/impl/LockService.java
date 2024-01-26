package com.example.Account.service.impl;


import com.example.Account.common.error.ErrorCode;
import com.example.Account.common.exception.ApiException;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.AccountException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Aspect
@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {

    @Autowired
    private final RedissonClient redissonClient;

    public String lock(String accountNumber){
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));

        try {
            boolean isLock = lock.tryLock(1,1000, TimeUnit.SECONDS);

            if (!isLock) {
                log.error("Lock acquistion failed");
                throw new ApiException(ErrorCode.SERVER_ERROR);
            }
        }catch (Exception e){
            log.error("Redis lock failed");
        }

        return "Lock success";
    }

    public void unlock(String accountNumber){
        log.debug("Unlock for accountNUmber : {}",accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    public String getLockKey(String accountNumber){
        return "ACLK" + accountNumber;
    }
}