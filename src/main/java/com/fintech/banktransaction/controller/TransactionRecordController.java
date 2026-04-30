package com.fintech.banktransaction.controller;

import com.fintech.banktransaction.dto.TransactionRecordDTO;
import com.fintech.banktransaction.service.TransactionRecordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Payroll", description = "Payroll Application APIs")


@RestController
// 体现了完整的资源层级关系：customer-account-transactionrecord（在这里并不需要银行的关系）
@RequestMapping("/api/customer/{fromCustomerId}/account/{accountId}/transaction_record")
public class TransactionRecordController {

    //1 Service
    private final TransactionRecordService transactionRecordService;

    //2 Controller
    @Autowired
    public TransactionRecordController(TransactionRecordService transactionRecordService) {
        this.transactionRecordService = transactionRecordService;
    }

    //3 业务：转账操作 POST /transfer
    //处理账户间的资金转移
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@PathVariable Long fromCustomerId, // 转出客户ID
                                      @PathVariable("accountId") Long fromAccountId, // 转出账户ID (重命名路径变量：原来是account现在是fromaccount)
                                      @Valid @RequestBody TransferRequest request) { // 转账请求数据集
        // 3.1 执行转账交易：从fromAccount转到toAccount
        TransactionRecordDTO transactionRecord = transactionRecordService
                .performTransaction(fromCustomerId, fromAccountId,//转出方信息
                        request.toCustomerId, request.toAccountId, request.bankAccountId, // 转入方信息&收款银行(可能)
                        request.amount, "Transfer.",request.percentage);//转账金额，转账备注，手续费汇率(可能)
        //3.2 返回执行结果和数据
        return ResponseEntity.ok(transactionRecord);
    }


    //4 业务：存款操作 POST /deposit
    //向指定账户存入资金
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@PathVariable("fromCustomerId") Long toCustomerId,
                                     @PathVariable("accountId") Long toAccountId,
                                     @Valid @RequestBody DepositWithdrawRequest request) {
        TransactionRecordDTO transactionRecord = transactionRecordService
                .performTransaction(null, null,
                        toCustomerId, toAccountId,null,
                        request.amount, "Deposit.",0.0);
        return ResponseEntity.ok(transactionRecord);
    }

    // 转账请求数据结构
    public static class TransferRequest {//request就是需要从客户那获得的数据
        @NotNull(message = "toCustomerId is required.")
        @Positive(message = "toCustomerId must be positive.")
        public Long toCustomerId;
        @NotNull(message = "toAccountId is required.")
        @Positive(message = "toAccountId must be positive.")
        public Long toAccountId;
        @NotNull(message = "amount is required.")
        @Positive(message = "amount must be positive.")
        public Double amount;
        @Positive(message = "bankAccountId must be positive when provided.")
        public Long bankAccountId;
        @NotNull(message = "percentage is required.")
        @DecimalMin(value = "0.0", message = "percentage must be between 0 and 1.")
        @DecimalMax(value = "1.0", message = "percentage must be between 0 and 1.")
        public Double percentage;
    }

    // 存款/取款请求数据结构
    public static class DepositWithdrawRequest {
        @NotNull(message = "amount is required.")
        @Positive(message = "amount must be positive.")
        public Double amount;
    }
}
