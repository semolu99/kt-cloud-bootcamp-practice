package com.example.my_api_server.lock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class SyncCounter {

    private int count = 0; //해당 공유 영역 값을 동시에 수정

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        int threadCount = 3;
        SyncCounter counter = new SyncCounter();

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

    private synchronized void increaseCount() {
        Thread.State state = Thread.currentThread().getState();
        log.info("state 1 = {}", state);
        synchronized (this) {
            log.info("state 2 = {}", state);
            count++;
        }
        log.info("state 3 = {}", state);
    }
}
