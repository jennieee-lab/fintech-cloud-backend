package com.fintech.banktransaction.repository;

import com.fintech.banktransaction.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data Access Object for customer database table.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    //继承了JpaRepository 已包含基础的 增删改查 操作
}
