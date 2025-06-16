package com.example.bank.service;


import com.example.bank.model.PageResponse;
import com.example.bank.model.Transaction;
import io.micrometer.common.util.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final Map<String, Transaction> transactionStore = new ConcurrentHashMap<>();

    // 创建交易
    public Transaction createTransaction(Transaction transaction) {
        if (transactionStore.containsKey(transaction.getId())) {
            throw new IllegalArgumentException("Transaction ID already exists");
        }
        validateParam(transaction);
        transactionStore.put(transaction.getId(), transaction);
        return transaction;
    }

    // 删除交易
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteTransaction(String id) {
        if (!transactionStore.containsKey(id)) {
            throw new IllegalArgumentException("Transaction not found");
        }
        transactionStore.remove(id);
    }

    // 更新交易
    @CacheEvict(value = "transactions", allEntries = true)
    public Transaction updateTransaction(Transaction updatedTransaction) {
        if (!transactionStore.containsKey(updatedTransaction.getId())) {
            throw new IllegalArgumentException("Transaction not found");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        updatedTransaction.setTimestamp(now.format(formatter));
        validateParam(updatedTransaction);
        transactionStore.put(updatedTransaction.getId(), updatedTransaction);
        return updatedTransaction;
    }

    @Cacheable(value = "transactions", key = "'all'")
    public Transaction listTransactionById(String id) {
        return transactionStore.get(id);
    }


    // 获取交易记录
    public PageResponse<Transaction> listTransactionsByPage(int page, int size) {
        List<Transaction> sortedTransactions = transactionStore.values().stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());

        // 手动实现分页（实际项目建议用JPA或MyBatis）
        int start = Math.min((int) (page * size), sortedTransactions.size());
        int end = Math.min(start + size, sortedTransactions.size());

        List<Transaction> pageContent = sortedTransactions.subList(start, end);
        return new PageResponse<>(pageContent, page, size, sortedTransactions.size(), (sortedTransactions.size() + size - 1) / size);
    }

    // 参数校验
    private void validateParam(Transaction transaction){
        if(StringUtils.isEmpty(transaction.getAccountId())){
            throw new IllegalArgumentException("accountId is null");
        }
        if(transaction.getAmount() == null){
            throw new IllegalArgumentException("amount is null");
        }
        if(transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0 ){
            throw new IllegalArgumentException("The amount is less than or equal to zero");
        }
        if(StringUtils.isEmpty(transaction.getType())){
            throw new IllegalArgumentException("type is null");
        }
    }


    public List<Transaction> listAllTransactions() {
        return new ArrayList<>(transactionStore.values());
    }
}
