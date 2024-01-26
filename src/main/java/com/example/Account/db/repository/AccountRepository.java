package com.example.Account.db.repository;


import com.example.Account.db.Account;
import com.example.Account.db.User;
import com.example.Account.model.AccountDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Integer countByUser(User user);
    Optional<Account> findFirstByOrderByIdDesc();
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAllByUser(User user);
}
