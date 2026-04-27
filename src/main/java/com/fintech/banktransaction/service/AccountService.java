package com.fintech.banktransaction.service;

import com.fintech.banktransaction.dto.AccountDTO;
import com.fintech.banktransaction.model.Account;
import com.fintech.banktransaction.model.Customer;
import com.fintech.banktransaction.repository.AccountRepository;
import com.fintech.banktransaction.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;//导入缓存注解

/**
 * Business logic for account management. 处理账户的创建和查询逻辑，关联客户与账户的关系
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    //依赖注入：注入数据库操作函数
    @Autowired
    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    //处理业务：根据客户 ID 创建账户
    @Transactional
    public AccountDTO createAccount(Long customerId, String accountName,String accountType) {
        Customer customer = customerRepository.getReferenceById(customerId);//查询客户是否存在
        Account account = new Account(customer, accountName, accountType);//用找出来的客户 账户名称 账户类型 来新建一个账户实体
        account = accountRepository.save(account);//通过数据库的save（）操作将实体保存到数据库中
        return new AccountDTO(account, true);//转换为AccountDTO返回
    }

    //处理业务：查询指定客户的指定账户
    //添加 @Cacheable 注解
    // value = "accountCache" 是在 Redis 里的分类名称
    // key = "#accountId" 表示用账户ID作为 Redis 的键
    @Transactional
    @Cacheable(value="accountCache", key = "#accountId")
    public AccountDTO getAccount(Long customerId, Long accountId) {
        // 👈 新增：这行打印非常重要！如果控制台没有打印这句话，说明完全没有查询数据库，直接从 Redis 极速返回了数据！
        System.out.println("====== ⚠️ 正在从 PostgreSQL 数据库查询账户信息: " + accountId + " ======");
        Account account = accountRepository.findByIdAndCustomer(accountId, customerRepository.getReferenceById(customerId)).orElseThrow();//验证账户是否属于该客户
        return new AccountDTO(account, true);//转换为AccountDTO返回
    }
}
