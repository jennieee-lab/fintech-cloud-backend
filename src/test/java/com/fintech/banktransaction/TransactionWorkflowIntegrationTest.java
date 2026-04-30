package com.fintech.banktransaction;

import com.fintech.banktransaction.dto.AccountDTO;
import com.fintech.banktransaction.dto.BankRevenueAccountDTO;
import com.fintech.banktransaction.dto.CustomerDTO;
import com.fintech.banktransaction.dto.TransactionRecordDTO;
import com.fintech.banktransaction.errors.InsufficientBalanceException;
import com.fintech.banktransaction.model.Account;
import com.fintech.banktransaction.model.BankRevenueAccount;
import com.fintech.banktransaction.repository.AccountRepository;
import com.fintech.banktransaction.repository.BankRevenueAccountRepository;
import com.fintech.banktransaction.service.AccountService;
import com.fintech.banktransaction.service.BankRevenueAccountService;
import com.fintech.banktransaction.service.CustomerService;
import com.fintech.banktransaction.service.TransactionRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TransactionWorkflowIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BankRevenueAccountService bankRevenueAccountService;

    @Autowired
    private TransactionRecordService transactionRecordService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BankRevenueAccountRepository bankRevenueAccountRepository;

    @Test
    void shouldProcessBusinessTransferAndCollectMerchantFee() {
        CustomerDTO sender = customerService.createCustomer("Sender", "Biz");
        AccountDTO senderAccount = accountService.createAccount(sender.getId(), "Sender Business", "business");
        CustomerDTO receiver = customerService.createCustomer("Receiver", "User");
        AccountDTO receiverAccount = accountService.createAccount(receiver.getId(), "Receiver Personal", "personal");
        BankRevenueAccountDTO bankRevenueAccount = bankRevenueAccountService.createBankRevenueAccount("Bank Revenue");

        transactionRecordService.performTransaction(
                null, null,
                sender.getId(), senderAccount.getId(), null,
                1000.0, "Deposit.", 0.0);

        TransactionRecordDTO transactionRecord = transactionRecordService.performTransaction(
                sender.getId(), senderAccount.getId(),
                receiver.getId(), receiverAccount.getId(), bankRevenueAccount.getId(),
                500.0, "Transfer.", 0.05);

        Account persistedSenderAccount = accountRepository.findById(senderAccount.getId()).orElseThrow();
        Account persistedReceiverAccount = accountRepository.findById(receiverAccount.getId()).orElseThrow();
        BankRevenueAccount persistedBankRevenueAccount = bankRevenueAccountRepository.findById(bankRevenueAccount.getId()).orElseThrow();

        assertThat(transactionRecord.getIsBusiness()).isTrue();
        assertThat(transactionRecord.getMerchantFee()).isEqualTo(25.0);
        assertThat(persistedSenderAccount.getBalance()).isEqualTo(500.0);
        assertThat(persistedReceiverAccount.getBalance()).isEqualTo(475.0);
        assertThat(persistedBankRevenueAccount.getBalance()).isEqualTo(25.0);
    }

    @Test
    void shouldRejectTransferWhenBalanceIsInsufficient() {
        CustomerDTO sender = customerService.createCustomer("Low", "Balance");
        AccountDTO senderAccount = accountService.createAccount(sender.getId(), "Sender Personal", "personal");
        CustomerDTO receiver = customerService.createCustomer("Target", "User");
        AccountDTO receiverAccount = accountService.createAccount(receiver.getId(), "Receiver Personal", "personal");

        assertThatThrownBy(() -> transactionRecordService.performTransaction(
                sender.getId(), senderAccount.getId(),
                receiver.getId(), receiverAccount.getId(), null,
                100.0, "Transfer.", 0.0))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("insufficient balance");
    }
}
