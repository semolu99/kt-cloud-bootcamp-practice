package com.example.my_api_server.controller;

import ch.qos.logback.core.util.StringUtil;
import com.example.my_api_server.service.MemberDBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberDBService memberService;

    @PostMapping
    public Long signUp(@Validated @RequestBody MemberSignUpDto dto) {
        if (StringUtil.isNullOrEmpty(dto.email()) || StringUtil.isNullOrEmpty(dto.password())) {
            throw new RuntimeException("email or password가 빈값이 되면 안됩니다.");
        }

        try {
            return memberService.signUp(dto.email(), dto.password());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @GetMapping("/{id}")
//    public Member findMember(@PathVariable Long id) {
//        return memberService.findMember(id);
//    }

    @GetMapping("/test")
    public void test() {
        memberService.tx1();
    }
}
