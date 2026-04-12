package com.example.my_api_server.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExceptionTest {

    private int count = 0;

    public static void main(String[] args) {
        ThreadExceptionTest t = new ThreadExceptionTest();
        int threadCount = 10000;
        ExecutorService es = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            es.submit(t::increase);

        }

        es.shutdown();

        System.out.println("실행 완료!");
    }

    private void increase() {
        count++;
    }
}
