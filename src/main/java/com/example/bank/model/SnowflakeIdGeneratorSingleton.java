package com.example.bank.model;

public class SnowflakeIdGeneratorSingleton {
    private static final SnowflakeIdGenerator INSTANCE = new SnowflakeIdGenerator(1, 1); // 传入数据中心ID和机器ID

    private SnowflakeIdGeneratorSingleton() {}

    public static SnowflakeIdGenerator getInstance() {
        return INSTANCE;
    }
}
