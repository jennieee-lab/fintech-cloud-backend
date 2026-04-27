package com.fintech.banktransaction.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity object for transaction_record database table.
 */
@Getter
@NoArgsConstructor
@Entity
public class TransactionRecord {
    //attribute
    @Id
    @GeneratedValue
    private long id;//PK id

    @Column(nullable = false)//NOT NULL
    private Double amount;//amount

    private String memo;//备注

    @Column(nullable = false)//NOT NULL
    private LocalDateTime time;//time

    //---------------------------------------
    @Column(nullable = false)//NOT NULL
    private Boolean isBusiness;

    @Column(nullable = false)//NOT NULL
    private Double percentage;

    @Column(nullable = false)//NOT NULL
    private Double merchantFee;
    //---------------------------------------

    //relationship
    @ManyToOne
    @JoinColumn
    private Account toAccount;//toaccount 转入账户

    @ManyToOne
    @JoinColumn
    private Account fromAccount;//fromaccount 转出账户

    @ManyToOne
    @JoinColumn
    private BankRevenueAccount bankRevenueAccount;//银行收入账户

    @Version
    private int version;//lock version

    //init function
    public TransactionRecord(Double amount, Account toAccount, Account fromAccount, BankRevenueAccount bankRevenueAccount, String memo,Boolean isBusiness,Double percentage, Double merchantFee) {
        this.amount = amount;
        this.time = LocalDateTime.now();
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.bankRevenueAccount = bankRevenueAccount;
        this.memo = memo;

        //---------------------------------------
        this.isBusiness = isBusiness;
        this.percentage = percentage;
        this.merchantFee = merchantFee;
        //---------------------------------------
    }
}
