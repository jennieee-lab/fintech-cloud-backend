package com.fintech.banktransaction.repository;

import com.fintech.banktransaction.model.Account;
import com.fintech.banktransaction.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data Access Object for account database table.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Spring Data JPA will automatically generate the implementation.
    // Find more about it at https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
    Optional<Account> findByIdAndCustomer(long id, Customer customer);
    // 注释说明：Spring Data JPA会自动生成该方法的实现
    // 可参考官方文档了解查询方法生成规则：https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html

    // 自定义查询方法：根据id和customer查询账户
    // 方法名遵循Spring Data JPA命名规范，会自动解析为SQL查询：where id = ?1 and customer = ?2
    // 返回值Optional<Account>表示查询结果可能存在或不存在（避免空指针异常）
}
