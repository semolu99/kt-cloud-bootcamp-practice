package com.example.my_api_server.lock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Slf4j
public class ReentrantCounter {

    private final ReentrantLock lock = new ReentrantLock();

    private int count = 0; //해당 공유 영역 값을 동시에 수정

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        int threadCount = 1000;
        ReentrantCounter counter = new ReentrantCounter();

        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(counter::increaseCount);
            thread.start();
            threads.add(thread);
        }
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("기대값 : {}", threadCount);
        log.info("실제값 : {}", counter.getCount());
    }

    private void increaseCount() {
        this.lock.lock();
        try {
            if (this.lock.tryLock(3, TimeUnit.SECONDS)) {
                try {
                    log.info("lock 획득 후 연산 작업 시작");
                    this.count++;
                    Thread.sleep(4000);
                } finally {
                    this.lock.unlock();
                }
            } else {
                log.info("3초 안에 Lock 획득 불가");
            }
        } catch (InterruptedException e) {
            log.info("작업중단");
            throw new RuntimeException(e);
        } finally {
            this.lock.unlock();
        }
    }
}
