package com.fintech.banktransaction.controller;

import com.fintech.banktransaction.dto.BankRevenueAccountDTO;
import com.fintech.banktransaction.service.BankRevenueAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController// @RestController = @Controller + @ResponseBody 标识这是一个REST API控制器，返回值自动序列化为JSON
@RequestMapping("/api/bankrevenueaccount")// 定义基础请求路径 @RequestMapping：定义控制器级别的 请求路径前缀
public class BankRevenueAccountController {
    private BankRevenueAccountService bankRevenueAccountService;
    @Autowired
    public void setBankRevenueAccountService(BankRevenueAccountService bankRevenueAccountService) {this.bankRevenueAccountService = bankRevenueAccountService;}
    @PostMapping//业务：创建银行账户
    public ResponseEntity<BankRevenueAccountDTO> createBankRevenueAccount(@RequestBody CreateBankRevenueAccountRequest request) {
        BankRevenueAccountDTO bankRevenueAccountDTO=bankRevenueAccountService.createBankRevenueAccount(request.bankAccountName);
        return ResponseEntity.ok(bankRevenueAccountDTO);
    }
    //参数数据结构
    public static class CreateBankRevenueAccountRequest {
        public String bankAccountName;
    }
}

