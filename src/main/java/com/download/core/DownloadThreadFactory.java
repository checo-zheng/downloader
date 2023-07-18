package com.download.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 憨憨
 */
public class DownloadThreadFactory implements ThreadFactory {
//    private ThreadFactory threadFactory;
    private String name;
    private AtomicInteger threadNum = new AtomicInteger();

    public DownloadThreadFactory( String name) {
//        this.threadFactory = threadFactory;
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
//        Thread thread = threadFactory.newThread(r);
        Thread thread = new Thread(r);
        thread.setName(name + "-" + threadNum.incrementAndGet());
        return thread;
    }
}
