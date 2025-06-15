package com.example.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;  // 启用缓存（可选）

@SpringBootApplication
@EnableCaching
public class BankTransactionApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankTransactionApplication.class, args);
    }
}
