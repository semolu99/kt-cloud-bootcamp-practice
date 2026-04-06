package com.example.my_api_server.common;

import com.example.my_api_server.entity.Member;

public class MemberFixture {
    public static Member.MemberBuilder defaultMember() {
        return Member.builder()
                .email("test1@gmail.com");
    }
}
