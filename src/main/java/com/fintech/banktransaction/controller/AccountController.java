package com.fintech.banktransaction.controller;

import com.fintech.banktransaction.dto.AccountDTO;
import com.fintech.banktransaction.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Interface 账户接口控制器
 */
@RestController
@RequestMapping("/api/customer/{customerId}/account")// 体现了资源的层级关系：客户 -> 账户
public class AccountController {

    //1 定义service
    private final AccountService accountService;

    //2 定义controller
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    //3 业务：创建账户 POST /api/customer/{customerId}/account
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(// @PathVariable：提取URL路径中的变量(customerId)
            @PathVariable Long customerId, @Valid @RequestBody CreateAccountRequest request) {
        //3.1 调用服务层创建账户（1解析请求中的参数：accountname/accounttype 2传入解析后参数，调用service层函数创建账户）
        AccountDTO account = accountService.createAccount(customerId, request.accountName,request.accountType);
        //3.2 创建成功后HTTP返回 200状态码和数据（accountDTO-会被序列化位JSON响应）
        return ResponseEntity.ok(account);
    }

    //4 业务：获取指定账户详情 GET /api/customer/{customerId}/account/{accountId}
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDTO> getAccount(
            //4.1从路径中提取两个参数
            @PathVariable Long customerId,
            @PathVariable Long accountId) {
        //4.2根据 客户ID 和 账户ID（确保账户归属验证）获取账户实体的信息
        AccountDTO account = accountService.getAccount(customerId, accountId);
        //4.3 创建成功后HTTP返回 200状态码和数据（accountDTO-会被序列化位JSON响应）
        return ResponseEntity.ok(account);
    }

    //5 创建 账户请求时的参数列表（数据结构）
    public static class CreateAccountRequest {
        @NotBlank(message = "accountName is required.")
        public String accountName;
        @NotBlank(message = "accountType is required.")
        public String accountType;
    }
}
