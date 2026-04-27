package com.fintech.banktransaction.repository;

import com.fintech.banktransaction.model.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data Access Object for transaction_record database table.
 */
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    /**
     * 这是一个文档注释，说明该接口是transaction_record数据库表的数据访问对象
     */
// 定义TransactionRecordRepository接口，继承JpaRepository
// 泛型参数分别为：操作的实体类TransactionRecord，实体类的主键类型Long
// 继承后自动拥有JpaRepository提供的增删改查等数据库操作方法
}
