package com.fintech.banktransaction.controller;

import com.fintech.banktransaction.dto.TransactionRecordDTO;
import com.fintech.banktransaction.service.TransactionRecordService;
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
                                      @RequestBody TransferRequest request) { // 转账请求数据集
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
                                     @RequestBody DepositWithdrawRequest request) {
        TransactionRecordDTO transactionRecord = transactionRecordService
                .performTransaction(null, null,
                        toCustomerId, toAccountId,null,
                        request.amount, "Deposit.",0.0);
        return ResponseEntity.ok(transactionRecord);
    }

    // 转账请求数据结构
    public static class TransferRequest {//request就是需要从客户那获得的数据
        public long toCustomerId;
        public long toAccountId;
        public double amount;
        public long bankAccountId;
        public double percentage;
    }

    // 存款/取款请求数据结构
    public static class DepositWithdrawRequest {
        public double amount;
    }
}
