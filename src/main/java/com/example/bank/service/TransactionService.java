package com.example.bank.service;


import com.example.bank.model.PageResponse;
import com.example.bank.model.Transaction;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    // 线程安全的内存存储，ConcurrentHashMap 保证并发安全
    private final Map<String, Transaction> transactionStore = new ConcurrentHashMap<>();

    // 创建交易（幂等性：通过 ID 避免重复）
    public Transaction createTransaction(Transaction transaction) {
        if (transactionStore.containsKey(transaction.getId())) {
            throw new IllegalArgumentException("Transaction ID already exists");
        }
        transactionStore.put(transaction.getId(), transaction);
        return transaction;
    }

    // 删除交易（校验存在性）
    @CacheEvict(value = "transactions", allEntries = true) // 删除缓存
    public void deleteTransaction(String id) {
        if (!transactionStore.containsKey(id)) {
            throw new IllegalArgumentException("Transaction not found");
        }
        transactionStore.remove(id);
    }

    // 修改交易（先校验存在性）
    @CacheEvict(value = "transactions", allEntries = true) // 删除缓存
    public Transaction updateTransaction(String id, Transaction updatedTransaction) {
        if (!transactionStore.containsKey(id)) {
            throw new IllegalArgumentException("Transaction not found");
        }
        // 替换内容（ID 保持不变）
        updatedTransaction.setId(id);
        transactionStore.put(id, updatedTransaction);
        return updatedTransaction;
    }

    // 查询所有交易（缓存优化）
    @Cacheable(value = "transactions", key = "'all'")
    public Transaction listTransactionById(String id) {
        return transactionStore.get(id);
    }


    // 分页查询交易（按时间倒序）
    public PageResponse<Transaction> listTransactionsByPage(int page, int size) {
        // 获取所有交易并按时间倒序排序
        List<Transaction> sortedTransactions = transactionStore.values().stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());

        // 计算分页信息
        int totalElements = sortedTransactions.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // 修正页码越界问题
        page = Math.max(1, page);
        page = Math.min(page, totalPages);

        // 计算分页索引
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalElements);

        // 获取分页内容
        List<Transaction> content = (Math.abs(startIndex) >= endIndex)
                ? Collections.emptyList()
                : sortedTransactions.subList(startIndex, endIndex);

        return new PageResponse<>(content, page, size, totalElements, totalPages);
    }


}
