package com.fintech.banktransaction.dto;

import com.fintech.banktransaction.model.BankRevenueAccount;
import com.fintech.banktransaction.model.TransactionRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; // 导入 NoArgsConstructor

import java.util.HashSet;
import java.util.Set;
import java.io.Serializable; // 导入 Serializable

/**
 * Data Transfer Object for Account.
 */
@NoArgsConstructor // 无参构造
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)// 只序列化非空集合/字符串
public class BankRevenueAccountDTO implements Serializable { // 实现 Serializable

    private static final long serialVersionUID = 1L; //序列化版本号

    private Long id;
    private String bankAccountName;
    private Double balance;

    /**
     * The set of transaction records associated with the account.
     */
    private Set<TransactionRecordDTO> transactionRecords = new HashSet<>();
    // 说明：
    // 1. 用Set而非List：避免交易记录重复（Set天然去重，适合"无重复关联数据"场景）
    // 2. 用TransactionRecordDTO：不直接传输TransactionRecord实体，隐藏交易记录的冗余字段
    // 3. 初始化HashSet：避免空指针异常（即使没有交易记录，也能返回空集合而非null）

    /**
     * Constructs an PersonalAccounDTO from an PersonalAccount entity.
     *
     * @param BankRevenueAccountEntity the PersonalAccount entity//定义参数：是个人账户的实体
     */
    public BankRevenueAccountDTO(BankRevenueAccount BankRevenueAccountEntity) {//不包含关联实体的构造函数，基础构造函数
        this(BankRevenueAccountEntity, false);
        // 作用：提供简化的创建方式，满足"只需要账户基本信息"的场景（如列表展示)
    }

    /**
     * Constructs an AccountDTO from an Account entity.
     *
     * @param BankRevenueAccountEntity          the PersonalAccount entity//定义参数：是个人账户的实体
     * @param includeRelatedEntities whether to include related entities//定义参数：是否包含关联实体
     */
    public BankRevenueAccountDTO(BankRevenueAccount BankRevenueAccountEntity, boolean includeRelatedEntities) {//包含关联实体的构造函数
        this.id = BankRevenueAccountEntity.getId();
        this.balance = BankRevenueAccountEntity.getBalance();
        this.bankAccountName = BankRevenueAccountEntity.getBankAccountName();

        //关联实体的内容
        if (includeRelatedEntities) {//相关外键的关联信息：如客户个人信息，转账记录
            // 2 转换交易记录：将Account实体中的"转出/转入交易记录"转为TransactionRecordDTO
            for (TransactionRecord toTransactionRecord : BankRevenueAccountEntity.getBankTransactionRecords()) {
                transactionRecords.add(new TransactionRecordDTO(toTransactionRecord));
            }
        }
    }
}



