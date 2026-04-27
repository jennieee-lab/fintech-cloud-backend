package com.fintech.banktransaction.controller;

import com.fintech.banktransaction.dto.CustomerDTO;
import com.fintech.banktransaction.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController// @RestController = @Controller + @ResponseBody 标识这是一个REST API控制器，返回值自动序列化为JSON
@RequestMapping("/api/customer")// 定义基础请求路径 @RequestMapping：定义控制器级别的 请求路径前缀
public class CustomerController {

    private final CustomerService customerService;//声明service 需要用到service层的服务

    @Autowired//注入控制器
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping//业务：创建客户 处理Post请求到/api/customer
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CreateCustomerRequest request) {
        // @RequestBody：将HTTP请求体中的JSON自动反序列化为Java对
        // 1.1接收json解析出来的数据：firstName lastName
        // 1.2并且调用服务层方法创建客户，传入firstName和lastName
        CustomerDTO customerDTO = customerService.createCustomer(request.firstName, request.lastName);
        // 2.1 ResponseEntity.ok()：返回HTTP 200状态码和数据
        // 2.2 Spring会自动将CustomerDTO序列化为JSON响应
        return ResponseEntity.ok(customerDTO);
    }

    // 内部静态类:用于接收创建客户的请求参数 // 使用public字段简化JSON反序列化
    public static class CreateCustomerRequest {
        public String firstName;
        public String lastName;
    }

}
