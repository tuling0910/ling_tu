package com.example.bank.model;

public class SnowflakeIdGenerator {
    // 起始时间戳，2020-01-01 00:00:00
    private final long startTimeStamp = 1577836800000L;

    // 机器ID所占位数
    private final long workerIdBits = 5L;
    // 数据中心ID所占位数
    private final long dataCenterIdBits = 5L;
    // 序列号所占位数
    private final long sequenceBits = 12L;

    // 机器ID最大值 31 (2^5-1)
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    // 数据中心ID最大值 31 (2^5-1)
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    // 机器ID向左移位数
    private final long workerIdShift = sequenceBits;
    // 数据中心ID向左移位数
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    // 时间戳向左移位数
    private final long timestampShift = sequenceBits + workerIdBits + dataCenterIdBits;

    // 序列号掩码 4095 (2^12-1)
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 工作机器ID
    private final long workerId;
    // 数据中心ID
    private final long dataCenterId;
    // 序列号
    private long sequence = 0L;
    // 上次生成ID的时间戳
    private long lastTimestamp = -1L;

    // 构造函数
    public SnowflakeIdGenerator(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Worker ID 不能大于 " + maxWorkerId + " 或小于 0");
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException("DataCenter ID 不能大于 " + maxDataCenterId + " 或小于 0");
        }

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    // 生成下一个ID
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        // 处理时钟回拨
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨，拒绝生成ID " + (lastTimestamp - currentTimestamp) + " 毫秒");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 当前毫秒内序列号用尽，等待下一毫秒
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，重置序列号
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // 按规则组合各部分生成ID
        return ((currentTimestamp - startTimeStamp) << timestampShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    // 等待下一毫秒
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    // 测试
    public static void main(String[] args) {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);

        for (int i = 0; i < 10; i++) {
            System.out.println(idGenerator.nextId());
        }
    }
}
