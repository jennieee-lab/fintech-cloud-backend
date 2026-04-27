package com.fintech.banktransaction.service;

import com.fintech.banktransaction.dto.CustomerDTO;
import com.fintech.banktransaction.model.Customer;
import com.fintech.banktransaction.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business logic for customer management. 处理客户的创建逻辑
 */
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    //final 关键字修饰的变量只能被赋值一次，赋值后无法再指向其他对象
    //通过强制依赖的不可变性，保证代码的安全性、可读性和线程安全，
    // 同时明确依赖的初始化时机，符合 “依赖注入” 和 “不可变对象” 的设计原则，
    // 是 Spring 服务层代码的最佳实践之一

    //注入数据库操作的函数
    @Autowired //Repository 注入：CustomerService中注入CustomerRepository--无需手动new对象，降低代码耦合
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;//--CustomerService需要操作客户数据（保存、查询），通过注入CustomerRepository（数据访问层接口），实现对数据库的操作，无需关心具体数据库实现
    }

    //核心业务：创建客户
    @Transactional
    public CustomerDTO createCustomer(String firstName, String lastName) {
        //main function
        Customer customer = new Customer(firstName, lastName);
        customer = customerRepository.save(customer);
        return new CustomerDTO(customer);
    }
}
