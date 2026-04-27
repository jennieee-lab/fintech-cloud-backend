package com.fintech.banktransaction.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Entity object for account database table.
 */
@Getter//Lombok 的@Getter已经生成了所有属性的 getter，满足正常的数据访问需求
//如果需要访问Account对象的属性，直接使用account.getBalance()、account.getCustomer()等即可。
@NoArgsConstructor
@Entity
public class Account {

    //attribute
    @Id
    @GeneratedValue
    private long id;//primary key id

    @Column(nullable = false)//NOT NULL
    private String accountName;//name

    @Column(nullable = false)//NOT NULL
    private String accountType;//type

    @Column(nullable = false)//NOT NULL
    private Double balance = 0.0;//balance

    @Version
    private int version;//lock version


    //relationship
    //account to customer
    @ManyToOne//多对一关系（多个账户属于一个客户）--> n-account 1:customer
    @JoinColumn(nullable = false)//FOREIGN KEY:(JoinColum()) NOT NULL(nullable = false)
    private Customer customer;

    //transactionRecord to account --> 1:account n:record
    @OneToMany(mappedBy = "fromAccount")// 作为转出账户的交易记录集合 便于查询该账户的转账记录
    private Collection<TransactionRecord> fromTransactionRecords;

    @OneToMany(mappedBy = "toAccount") // 作为转入账户的交易记录集合 便于查询该账户的转账记录
    private Collection<TransactionRecord> toTransactionRecords;


    //initial function
    public Account(Customer customer, String accountName,String accountType) {
        this.customer = customer;
        this.accountName = accountName;
        this.accountType = accountType;
        this.fromTransactionRecords = new ArrayList<>();
        this.toTransactionRecords = new ArrayList<>();
    }

    // 业务方法：修改账户余额
    public void modifyBalance(Double amount) {
        this.balance = this.balance + amount;
    }
}
