package com.example.demo.main.util;

import java.util.concurrent.*;

public class ThreadPool {

    public static ExecutorService pool;

    static {
        pool = new ThreadPoolExecutor(120, 200, 2000, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}