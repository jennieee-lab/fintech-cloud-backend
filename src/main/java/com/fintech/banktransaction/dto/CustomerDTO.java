package com.fintech.banktransaction.dto;

import com.fintech.banktransaction.model.Account;
import com.fintech.banktransaction.model.Customer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;//替换：导入 NoArgsConstructor
import lombok.Setter;

import java.io.Serializable; // 导入 Serializable
import java.util.ArrayList;
import java.util.Collection;

/**
 * Data Transfer Object for Customer.
 */
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO implements Serializable {// 实现 Serializable

    private static final long serialVersionUID = 1L; // 序列化版本号

    private Long id;
    private String firstName;
    private String lastName;
    private Collection<AccountDTO> accounts = new ArrayList<>();//这说明CustomerDTO的设计目标是在传递客户数据时，必须包含其关联的所有账户信息（即默认级联转换子对象）

    public CustomerDTO(Customer customerEntity) {
        this.id = customerEntity.getId();
        this.firstName = customerEntity.getFirstName();
        this.lastName = customerEntity.getLastName();
        for (Account account : customerEntity.getAccounts()) {
            this.accounts.add(new AccountDTO(account));//还没写好personalaccuntdtp
        }
    }
}
