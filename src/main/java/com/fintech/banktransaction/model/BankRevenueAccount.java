package com.fintech.banktransaction.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Entity object for account database table.
 */
@Getter
@NoArgsConstructor
@Entity
public class BankRevenueAccount {
    //attribute
    @Id//如果是主键，必须要写这个（相当于主键标识）
    @GeneratedValue
    private long id;//primary key id

    @Column(nullable = false)//NOT NULL
    private String bankAccountName;//name

    @Column(nullable = false)//NOT NULL
    private Double balance = 0.0;//balance

    @Version
    private int version;//lock version


//    //relationship(need to be modified)
//    @ManyToOne//多对一关系（多个账户属于一个客户）
//    @JoinColumn(nullable = false)//FOREIGN KEY NOT NULL
//    private Customer customer;


    @OneToMany(mappedBy = "bankRevenueAccount") // 作为转入账户的交易记录集合
    private Collection<TransactionRecord> bankTransactionRecords;

    //initial function
    public BankRevenueAccount(String bankAccountName) {//name是创建账户时由前端传入、经过后端业务层传递的 “账户名称”：例如什么工资卡，商业卡 前端用户输入 → Controller 接收 → Service 处理 → 构造 Account 对象
//        this.customer = customer;
        this.bankAccountName = bankAccountName;
        this.bankTransactionRecords = new ArrayList<>();
    }

    // 业务方法：修改账户余额
    public void modifyBalance(Double amount) {
        this.balance = this.balance + amount;
    }

}

