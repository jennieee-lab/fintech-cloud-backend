package com.fintech.banktransaction.service;

import com.fintech.banktransaction.dto.BankRevenueAccountDTO;
import com.fintech.banktransaction.model.BankRevenueAccount;
import com.fintech.banktransaction.repository.BankRevenueAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankRevenueAccountService {
    /**
     * Business logic for bank account management. 处理银行账户的创建逻辑
     */
    private final BankRevenueAccountRepository bankRevenueAccountRepository;

    //创建数据库操作对象
    @Autowired
    public BankRevenueAccountService(BankRevenueAccountRepository bankRevenueRepository) {
        this.bankRevenueAccountRepository = bankRevenueRepository;
    }
    public BankRevenueAccountDTO createBankRevenueAccount(String bankAccountName) {
        BankRevenueAccount bankRevenueAccount = new BankRevenueAccount(bankAccountName);//1 新建一个实体 新建操作：操作的是实体 但传出的是DTO
        bankRevenueAccount = this.bankRevenueAccountRepository.save(bankRevenueAccount);//2 实体更新进数据库
        return new BankRevenueAccountDTO(bankRevenueAccount);//3 返回数据操作对象DTO
    }
}
