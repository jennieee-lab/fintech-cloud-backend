package com.fintech.banktransaction.dto;

import com.fintech.banktransaction.model.Account;
import com.fintech.banktransaction.model.BankRevenueAccount;
import com.fintech.banktransaction.model.TransactionRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable; // 导入 Serializable
import java.time.LocalDateTime;

/**
 * Data Transfer Object for TransactionRecord.
 */
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRecordDTO implements Serializable { // 实现 Serializable

    private static final long serialVersionUID = 1L; // 序列化版本号

    private long id;
    private Double amount;
    private String memo;
    private LocalDateTime time;
    private Boolean isBusiness;
    private Double percentage;
    private Double merchantFee;
    private AccountDTO toAccount;
    private AccountDTO fromAccount;
    private BankRevenueAccountDTO bankRevenueAccount;

    public TransactionRecordDTO(TransactionRecord entity) {
        this.id = entity.getId();
        this.memo = entity.getMemo();
        this.amount = entity.getAmount();
        this.time = entity.getTime();
        this.isBusiness = entity.getIsBusiness();
        this.percentage = entity.getPercentage();
        this.merchantFee = entity.getMerchantFee();

        Account toAccount = entity.getToAccount();
        if (toAccount != null) {
            this.toAccount = new AccountDTO(toAccount);
        }
        Account fromAccount = entity.getFromAccount();
        if (fromAccount != null) {
            this.fromAccount = new AccountDTO(fromAccount);
        }
        BankRevenueAccount bankRevenueAccount = entity.getBankRevenueAccount();
        if (bankRevenueAccount != null) {
            this.bankRevenueAccount=new BankRevenueAccountDTO(bankRevenueAccount);
        }
    }
}
