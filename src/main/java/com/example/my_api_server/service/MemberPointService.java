package com.example.my_api_server.service;

import com.example.my_api_server.entity.Member;
import com.example.my_api_server.repo.MemberDBRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberPointService {

    private final MemberDBRepo memberDBRepo;

    @Transactional(propagation = Propagation.REQUIRES_NEW) //새로운 트랜젝션을 만드는 것
    public void changeAllUserData() {
        List<Member> members = memberDBRepo.findAll();

        //뭔가 값을 바꿨다고 가정
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void supportTxTest() {
        memberDBRepo.findAll();
    }

    @Transactional(timeout = 2)
    public void timeout() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        memberDBRepo.findAll();
    }
}
