package com.example.bank.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Transaction {
    // 交易 ID（唯一标识，可自动生成）
    private String id ;
    // 交易类型（如 DEPOSIT、WITHDRAWAL、TRANSFER 等）
    private String type;
    // 金额
    private BigDecimal amount;
    // 交易时间（默认当前时间）
    private LocalDateTime timestamp;
    // 描述（可选）
    private String accountId;

    public Transaction(String accountId, BigDecimal amount, String type) {
        this.id = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
}
