package com.example.my_api_server.repo;

import com.example.my_api_server.entity.Member;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


//db 통신없이 간단하게 인메모리 DB를 사용해서 CRUD 해보기
//DAO : DB와 통신하는 객체
@Repository
public class MemberRepo {

    Map<Long, Member> members = new HashMap<>();

    //로직
    public Long saveMember(String email, String password) {
        Random random = new Random();
        long id = random.nextLong();
        Member member = Member.builder()
                .id(id)
                .email(email)
                .password(password)
                .build();

        members.put(id, member);
        return id;
    }

    //조회
    public Member findMember(Long id) {
        return members.get(id);
    }
}
