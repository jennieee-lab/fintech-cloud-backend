package com.fintech.banktransaction.repository;

import com.fintech.banktransaction.model.BankRevenueAccount;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BankRevenueAccountRepository extends JpaRepository<BankRevenueAccount, Long> {
    //基础的增删改查
}
