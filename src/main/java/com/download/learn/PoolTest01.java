package com.download.learn;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 憨憨
 */
public class PoolTest01 {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                2,
                3,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(2));

        Runnable r = () -> System.out.println(Thread.currentThread().getName());
        for (int i = 0; i < 4; i++) {
            threadPool.execute(r);
        }
    }
}
