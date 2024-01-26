package com.example.Account.db.repository;

import com.example.Account.db.Account;
import com.example.Account.db.Transaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Optional<Transaction> findByTransactionId(String transactionId);

}
