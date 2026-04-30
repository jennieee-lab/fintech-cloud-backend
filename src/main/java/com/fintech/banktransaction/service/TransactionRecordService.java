package com.fintech.banktransaction.service;

import com.fintech.banktransaction.dto.TransactionRecordDTO;
import com.fintech.banktransaction.errors.InsufficientBalanceException;
import com.fintech.banktransaction.errors.NegativeTransferAmountException;
import com.fintech.banktransaction.errors.ResourceNotFoundException;
import com.fintech.banktransaction.model.Account;
import com.fintech.banktransaction.model.BankRevenueAccount;
import com.fintech.banktransaction.model.Customer;
import com.fintech.banktransaction.model.TransactionRecord;
import com.fintech.banktransaction.repository.AccountRepository;
import com.fintech.banktransaction.repository.BankRevenueAccountRepository;
import com.fintech.banktransaction.repository.CustomerRepository;
import com.fintech.banktransaction.repository.TransactionRecordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

/**
 * Business logic for creating and managing transactions (transfer / deposit). 处理转账、存款等交易逻辑，包含业务校验和事务管理
 * 交易创建和管理的核心业务逻辑
 */
@Service
public class TransactionRecordService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final BankRevenueAccountRepository bankRevenueAccountRepository;

    // @PersistenceContext：注入JPA EntityManager
    // 用于执行底层的数据库操作和缓存管理
    @PersistenceContext
    private EntityManager entityManager;//用于手动刷新实体

    //注入4个操作数据表的函数
    @Autowired
    public TransactionRecordService(AccountRepository accountRepository, CustomerRepository customerRepository, TransactionRecordRepository transactionRecordRepository, BankRevenueAccountRepository bankRevenueAccountRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.bankRevenueAccountRepository = bankRevenueAccountRepository;
    }

    @Transactional//关键：确保整个交易过程的原子性 //参数：转出方 / 转入方的客户 ID、账户 ID，交易金额，备注 银行收款账户id
    @Caching(evict = {
            @CacheEvict(value = "accountCache", key = "#fromAccountId", condition = "#fromAccountId != null"),
            @CacheEvict(value = "accountCache", key = "#toAccountId", condition = "#toAccountId != null")
    })//缓存主动失效机制：如果转出账户存在，踢掉转出账户的缓存；如果转入账户存在，踢掉转入账户的缓存（避免脏读）
    public TransactionRecordDTO performTransaction(
            //main function
            Long fromCustomerId, Long fromAccountId,
            Long toCustomerId, Long toAccountId, Long bankAccountId,
            Double amount, String memo, Double percentage)
            throws InsufficientBalanceException, HttpClientErrorException
    {
        //1.数据验证：检查转账金额是不是正确的：大于0 正数
        if (amount <= 0) {
            throw new NegativeTransferAmountException();
        }
        validatePercentage(percentage);

        //存款：没有fromaccount/取款：没有toaccount --fromaccount和toaccount都有可能是null

        //2.处理转出账户
        Account fromAccount = null;//新建一个转出账户的类
        BankRevenueAccount bankRevenueAccount = null;//新建一个银行账户类
        Double march_fees = 0.0;
        Boolean isBusiness = false;
        if (fromAccountId != null)//如果有转出账户：确认交易类型 不是存款，是转账
        {
            //2.1根据账户id和客户id找出客户的账户
            fromAccount = getAccountOrThrow(fromCustomerId, fromAccountId);
            //本质是双重验证：
            //验证 fromAccountId 对应的账户确实存在；
            //同时验证该账户属于当前操作的客户（fromCustomerId 对应的客户）
            //银行转账时，不仅要验证 “卡号是否存在”，还要验证 “卡主是否是你本人”—— 避免他人盗用账户信息进行操作

            // 2.2关键操作：刷新实体状态，获取最新数据
            // 防止在高并发环境下读取到过期的余额信息
            entityManager.refresh(fromAccount);//手动刷新：确保获取数据库最新的账户余额（避免并发场景下的脏读）

            //2.3业务验证：检查转出账户够不够钱转账 need：余额>=转账金额
            if (fromAccount.getBalance() < amount)
            {
                throw new InsufficientBalanceException();
            }

            //2.4执行转账操作：扣减金额
            fromAccount.modifyBalance(-amount);//转出账户：扣减相应金额
            accountRepository.save(fromAccount);

            //*（商业账户是在作为转出账户时会扣除手续费）：手续费是跟转出账户绑定的
            //2.1.1如果转出账户是商业账户
            if("business".equalsIgnoreCase(fromAccount.getAccountType())){//不能用==来判断 if(Objects.equals(fromAccount.getAccountType(), "business"))
                isBusiness = true;
                if (bankAccountId != null) {//有银行账户
                    march_fees=amount*percentage;//计算手续费
                    bankRevenueAccount = bankRevenueAccountRepository.findById(bankAccountId)
                            .orElseThrow(() -> new ResourceNotFoundException("Bank revenue account", bankAccountId));//找出对应银行账户
                    bankRevenueAccount.modifyBalance(march_fees);//修改银行账户余额：+手续费
                    bankRevenueAccountRepository.save(bankRevenueAccount);
                    //不在这里直接修改amount是因为怕影响到 fromaccount不为空 但 toaccount==null的情况 即 取款：（！但商业账户取款呢？也会扣除手续费吗？取款金额怎么看？原本的代码里貌似没有处理取款金额的业务逻辑）
                    //如果在这写amount=amount-march_fee就会导致取款也会把amount值改了，但其实只有 接受转账的 收入账户 需要这个修改后的金额
                }//注意！这里没有设置抛出异常：如果没有输入银行账户（感觉可以默认银行账户）
            }
        }

        //3.处理转入账户
        Account toAccount = null;

        if (toAccountId != null) {//如果有转入账户：确认交易类型 不是取款，是转账

            //3.1根据账户id和客户id找出客户的账户
            toAccount = getAccountOrThrow(toCustomerId, toAccountId);
            //注意：转入账户无需refresh
            //数据库会以 “最后一次更新” 为准（新余额 = 旧余额 + 新增金额）；
            //即使有并发转入，最终结果也是 “多次增加的总和”（例如两次各转 100 元，最终余额正确增加 200 元），不会出现数据不一致。

            //3.2判断接收的转账类型，修改金额
            //*（作为收入账户：如果是接受的商业账户的转账，则转出的amount需要扣除手续费）
            if(fromAccount !=null && "business".equalsIgnoreCase(fromAccount.getAccountType())){
                Double receiveAmount = amount - march_fees;
                toAccount.modifyBalance(receiveAmount);//转入账户：增加相应金额
            }else {
                toAccount.modifyBalance(amount);//转入账户：增加相应金额
            }
            accountRepository.save(toAccount);
        }

        //4.创建交易记录
        TransactionRecord transactionRecord = new TransactionRecord(amount, toAccount, fromAccount,bankRevenueAccount, memo,isBusiness,percentage,march_fees);
        transactionRecordRepository.save(transactionRecord);

        //5.返回交易记录DTO
        return new TransactionRecordDTO(transactionRecord);
    }

    private Account getAccountOrThrow(Long customerId, Long accountId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        return accountRepository.findByIdAndCustomer(accountId, customer)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }

    private void validatePercentage(Double percentage) {
        if (percentage == null || percentage < 0 || percentage > 1) {
            throw new IllegalArgumentException("percentage must be between 0 and 1.");
        }
    }
}
