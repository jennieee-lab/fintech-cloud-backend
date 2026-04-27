package com.fintech.banktransaction.dto;

import com.fintech.banktransaction.model.Account;
import com.fintech.banktransaction.model.TransactionRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;//导入无参构造注解

import java.io.Serializable; //导入 Serializable
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for Account.
 */
@Getter
@Setter
@NoArgsConstructor // 加上无参构造，防止 Redis 反序列化时找不到默认构造器报错
@JsonInclude(Include.NON_EMPTY)// 只序列化非空集合/字符串
public class AccountDTO implements Serializable { // 实现 Serializable 接口

    //添加序列化版本号（Java 规范做法，防止类结构改变时反序列化崩溃）
    private static final long serialVersionUID = 1L;

    private Long id;
    private CustomerDTO customer;
    private String accountName;
    private String accountType;
    private Double balance;

    /**
     * The set of transaction records associated with the account.
     */
    private Set<TransactionRecordDTO> transactionRecords = new HashSet<>();


    /**
     * Constructs an AccountDTO from an Account entity.
     *
     * @param accountEntity the account entity
     */
    public AccountDTO(Account accountEntity) {
        this(accountEntity, false);
    }

    /**
     * Constructs an AccountDTO from an Account entity.
     *
     * @param accountEntity          the account entity
     * @param includeRelatedEntities whether to include related entities
     */
    public AccountDTO(Account accountEntity, boolean includeRelatedEntities) {
        //attribute
        this.id = accountEntity.getId();
        this.balance = accountEntity.getBalance();
        this.accountName = accountEntity.getAccountName();
        this.accountType = accountEntity.getAccountType();
        //foreign key/relationship
        if (includeRelatedEntities) {
            this.customer = new CustomerDTO(accountEntity.getCustomer());

            for (TransactionRecord fromTransactionRecord : accountEntity.getFromTransactionRecords()) {
                transactionRecords.add(new TransactionRecordDTO(fromTransactionRecord));
            }

            for (TransactionRecord toTransactionRecord : accountEntity.getToTransactionRecords()) {
                transactionRecords.add(new TransactionRecordDTO(toTransactionRecord));
            }
        }
    }
}