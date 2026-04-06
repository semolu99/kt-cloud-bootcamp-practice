package com.example.my_api_server.service;

import com.example.my_api_server.entity.Member;
import com.example.my_api_server.event.MemberSignUpEvent;
import com.example.my_api_server.repo.MemberDBRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberDBService {

    private final MemberDBRepo memberDBRepo;
    private final MemberPointService memberPointService;
    private final ApplicationEventPublisher publisher;

    /**
     * 1. @Transcational은 AOP로 돌아가서 begin() tran() commit()
     * 2. DB에는 commit 명령어가 실행되어야 테이블에 반영된다. (redo, undo log) -> 테이블에 저장 //commit이나 rollback없으면 트랜젝션 종료안됨
     * 3. jpa의 구현체인 하이버네이트와 엔티티매니저 JBDC
     */

    @Transactional
    public Long signUp(String email, String password) throws IOException {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();

        Member saveMember = memberDBRepo.save(member);

//        sendNotification();
        publisher.publishEvent(new MemberSignUpEvent(saveMember.getId(), saveMember.getEmail()));

        return saveMember.getId();
    }
//        memberPointService.changeAllUserData();

//        DB에 저장하다가 뭔가 오류 발생해서 예외터짐(Runtime 예외)
//        throw new RuntimeException("DB에 저장하다가 뭔가 오류가 발생해서 예외가 터짐");
//        throw new IOException("외부 API 호출하다가 I/O 예외가 터짐");
//        I/O익셉션은 우리측 문젝가 아니라 상태측 문제이기 때문에 상대측 서버가 헬스체크가 된다면, 다시 보내줘야하는 로직을 구성해야함(재전송 로직)

    public void sendNotification() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("알림 전송완료!");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) //새로운 트랜젝션을 만드는 것
    public void changeAllUserData() {
        List<Member> members = memberDBRepo.findAll();

        //뭔가 값을 바꿨다고 가정
    }

    //테스트 메서드
    @Transactional(propagation = Propagation.REQUIRED, timeout = 2)
    public void tx1() {
        List<Member> members = memberDBRepo.findAll();
        members.stream()
                .forEach((m) -> {
                    log.info("member id = {}", m.getId());
                    log.info("member email = {}", m.getEmail());
                });
        memberPointService.changeAllUserData(); // AOP

        memberPointService.timeout();
    }
}
