package com.example.bank.model;

import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private String timestamp;
    // 描述（可选）
    private String accountId;

    public Transaction(String accountId, BigDecimal amount, String type) {
        this.id = String.valueOf(SnowflakeIdGeneratorSingleton.getInstance().nextId());
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.timestamp = now.format(formatter);
    }
}
