package com.example.bank.service;


import com.example.bank.model.PageResponse;
import com.example.bank.model.Transaction;
import io.micrometer.common.util.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    public Transaction updateTransaction(String id, Transaction updatedTransaction) {
        if (!transactionStore.containsKey(id)) {
            throw new IllegalArgumentException("Transaction not found");
        }
        validateParam(updatedTransaction);
        updatedTransaction.setId(id);
        transactionStore.put(id, updatedTransaction);
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

        int totalElements = sortedTransactions.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        page = Math.max(1, page);
        page = Math.min(page, totalPages);

        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalElements);

        List<Transaction> content = (Math.abs(startIndex) >= endIndex)
                ? Collections.emptyList()
                : sortedTransactions.subList(startIndex, endIndex);

        return new PageResponse<>(content, page, size, totalElements, totalPages);
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
}
