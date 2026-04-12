package com.example.my_api_server.virtual_thread;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@Slf4j
public class VTClass {

    static final int TASK_COUNT = 1000;

    static final Duration IO_DURATION = Duration.ofSeconds(1);

    public static void main(String[] args) {
//        Thread.ofVirtual()
//                .name("가상 스레드1")
//                .start(VTClass::add);

//        log.info("[i/o] 플랫폼 스레드 시작!");
//        ioRun(Executors.newFixedThreadPool(200));
//
//        log.info("[i/o] 가상 스레드 시작!");
//        ioRun(Executors.newVirtualThreadPerTaskExecutor());
//
//        log.info("[CPU] 플랫폼 스레드 시작!");
//        cpuRun(Executors.newFixedThreadPool(200));
//
//        log.info("[CPU] 가상 스레드 시작!");
//        cpuRun(Executors.newVirtualThreadPerTaskExecutor());
//        log.info("[i/o] 플랫폼 스레드 피닝 시작!");
//        ioRunPinning(Executors.newFixedThreadPool(200));
//        log.info("[i/o] 가상 스레드 피닝 시작!");
//        ioRunPinning(Executors.newVirtualThreadPerTaskExecutor());

        log.info("[i/o] 플랫폼 스레드 피닝2 시작!");
        ioRunPinningRL(Executors.newFixedThreadPool(200));
        log.info("[i/o] 가상 스레드 피닝2 시작!");
        ioRunPinningRL(Executors.newVirtualThreadPerTaskExecutor());
    }

    public static void ioRun(ExecutorService es) {
        Instant start = Instant.now();

        try (es) {
            IntStream.range(0, TASK_COUNT).forEach(idx -> {
                es.submit(() -> {
                    try {
                        Thread.sleep(IO_DURATION);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
        }

        Instant end = Instant.now();
        System.out.printf("작업 완료 시간: %d ms%n", Duration.between(start, end).toMillis());
    }

    public static void cpuRun(ExecutorService es) {
        Instant start = Instant.now();

        try (es) {
            IntStream.range(0, TASK_COUNT).forEach(idx -> {
                es.submit(() -> {
                    // cpu 연산이 많다 (cpu bound)
                    for (int i = 0; i < 1000000; i++) {
                        int a = 1;
                        int b = 2;
                        int c = a + b;
                    }
                });
            });
        }

        Instant end = Instant.now();
        System.out.printf("작업 완료 시간: %d ms%n", Duration.between(start, end).toMillis());
    }

    public static void ioRunPinning(ExecutorService es) {
        Instant start = Instant.now();

        try (es) {
            IntStream.range(0, TASK_COUNT).forEach(idx -> {
                es.submit(() -> {
                    synchronized (es) {
                        try {
                            Thread.sleep(IO_DURATION);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            });
        }

        Instant end = Instant.now();
        System.out.printf("작업 완료 시간: %d ms%n", Duration.between(start, end).toMillis());
    }

    public static void ioRunPinningRL(ExecutorService es) {
        Instant start = Instant.now();

        try (es) {
            IntStream.range(0, TASK_COUNT).forEach(idx -> {
                es.submit(() -> {
                    ReentrantLock lock = new ReentrantLock();
                    lock.lock();
                    try {
                        Thread.sleep(IO_DURATION);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }

                });
            });
        }

        Instant end = Instant.now();
        System.out.printf("작업 완료 시간: %d ms%n", Duration.between(start, end).toMillis());
    }
}
