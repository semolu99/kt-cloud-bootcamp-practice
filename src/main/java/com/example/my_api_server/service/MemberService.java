package com.example.my_api_server.service;

import com.example.my_api_server.entity.Member;
import com.example.my_api_server.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//비지니스 로직 구성
@Service //빈으로 등록
@RequiredArgsConstructor //di
@Slf4j //로그
public class MemberService {
    private final MemberRepo memberRepo;

    /**
     * 회원가입 저장 후 알림으로 전송한다.
     *
     * @param email
     * @param password
     * @return memberId
     */

    public Long signUp(String email, String password) {
        //회원 저장 후 알림을 전송한다로 가정
        Long memberId = memberRepo.saveMember(email, password);

        log.info("회원가입한 member ID = {}", memberId);

        //알림전송
        sendNotification();

        return memberId;
    }

    public void sendNotification() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("알림 전송완료!");
    }

    /**
     * - Member 조회
     *
     * @param id
     * @return Member
     */
    public Member findMember(Long id) {
        Member member = memberRepo.findMember(id);
        return member;
    }
}
