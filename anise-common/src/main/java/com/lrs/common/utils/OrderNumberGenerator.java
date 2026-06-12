package com.lrs.common.utils;

import cn.hutool.core.util.IdUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 订单生成器
 * 理论上每秒最多能生成 1000 万（10,000,000）个订单号，这是它并发能力的上限
 * 理论极限：每毫秒 10000 个
 * 订单号的结构是：
 * 17 位时间戳（到毫秒）
 * 1 位机器 ID
 * 4 位序列号（0000 ~ 9999，共 10000 个）
 * 同一毫秒内，只要序列号没超过 9999，就能持续生成。一旦序列号达到 9999，新请求会进入 waitNextMillisecond() 自旋，直到进入下一毫秒才重置序列号为 0 继续生成。
 * <p>
 * 因此，单机（同一机器 ID） 下，每毫秒最多 10000 个 ID，换算成 QPS 就是：
 * 10000 × 1000 = 10,000,000 个/秒 （这个是理论，实际达不到，看服务器性能）
 * 这就是该生成器的理论最大吞吐量。
 * <p>
 * 注意：
 * 1、如果高并发建议使用雪花算法等其他算法
 * 2、如果是多实例，一定要修改机器码，不然会重复（不然同一毫秒内下单就会重复）
 * 3、单机版或小并发用这个够用了
 */
public class OrderNumberGenerator {
    // 日期格式（年月日时分秒毫秒）
    private static final String DATE_FORMAT = "yyyyMMddHHmmssSSS";
    // 最大序列号（4位）
    private static final int MAX_SEQUENCE = 9999;
    // 状态原子引用（包含时间戳和序列号）
    private static final AtomicReference<State> stateRef = new AtomicReference<>();

    static {
        // 初始化状态
        long currentMillis = System.currentTimeMillis();
        stateRef.set(new State(currentMillis, formatTimestamp(currentMillis), 0));
    }

    /**
     * 内部状态类
     * @param timestampMillis 时间戳（毫秒）
     * @param timestampStr    格式化时间字符串
     * @param sequence        当前序列号
     */
    private record State(long timestampMillis, String timestampStr, int sequence) {
    }

    public static String nextId() {
        return nextId("8");
    }

    /**
     * 生成唯一订单号
     *
     * @param machineId 2位数字机器标识
     * @return 23位数字订单号（17位时间 + 2位机器ID + 4位序列号）
     */
    public static String nextId(String machineId) {
        if (machineId == null || machineId.length() != 1 || !machineId.matches("\\d")) {
            throw new IllegalArgumentException("机器码可选：[0-9]中的一个");
        }
        while (true) {
            State currentState = stateRef.get();
            long nowMillis = System.currentTimeMillis();
            String nowStr;

            // 检查时钟回拨
            if (nowMillis < currentState.timestampMillis) {
                throw new IllegalStateException("时间回退了，拒绝生成ID");
            }
            int nextSeq;
            if (nowMillis == currentState.timestampMillis) {
                // 相同毫秒值，序列号 +1
                nextSeq = currentState.sequence + 1;
                if (nextSeq > MAX_SEQUENCE) {
                    waitNextMillisecond(nowMillis);
                    // 需要重试
                    continue;
                }
                nowStr = currentState.timestampStr;
            } else {
                nextSeq = 0;
                nowStr = formatTimestamp(nowMillis);
            }
            State newState = new State(nowMillis, nowStr, nextSeq);
            if (stateRef.compareAndSet(currentState, newState)) {
                return String.format("%s%s%04d", nowStr, machineId, nextSeq);
            }
        }
    }

    // 等待到下一毫秒
    private static void waitNextMillisecond(long currentMillis) {
        while (System.currentTimeMillis() == currentMillis) {
            // 自旋等待
        }
    }

    // 格式化时间戳
    private static String formatTimestamp(long millis) {
        return new SimpleDateFormat(DATE_FORMAT).format(new Date(millis));
    }

    public static void main(String[] args) throws InterruptedException {
        // 生成 5 个示例订单号
        System.out.println("=== 示例订单号 ===");
        for (int i = 0; i < 5; i++) {
            System.out.println(OrderNumberGenerator.nextId());
        }

        // 并发性能测试（10线程 × 1万次）
        int threadCount = 10;
        int perThread = 10_000;
        Thread[] threads = new Thread[threadCount];
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    nextId();
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        long end = System.currentTimeMillis();
        System.out.println("生成 " + (threadCount * perThread) + " 个订单号，耗时 " + (end - start) + " ms");
        System.out.println(IdUtil.getSnowflakeNextId());
    }
}