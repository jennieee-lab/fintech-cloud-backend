package com.fintech.banktransaction.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Entity object for customer database table.
 */
@Getter
@NoArgsConstructor
@Entity
public class Customer {
    @Id
    @GeneratedValue
    private long id;//id

    @Version
    private int version;//Lock version

    @Column(nullable = false)
    private String firstName;//firstname

    @Column(nullable = false)
    private String lastName;//lastname

    @OneToMany(mappedBy = "customer")
    private final Collection<Account> Accounts = new ArrayList<>();//Table name:Account; program varible:accounts

    public Customer(String af, String al) {//initialization
        this.firstName = af;
        this.lastName = al;
    }
}
