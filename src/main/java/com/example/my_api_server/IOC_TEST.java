package com.example.my_api_server;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Test")
@RequiredArgsConstructor //DI 자동으로 해주는 어노테이션으로 Lombok
public class IOC_TEST {
//    //1. 필드 주입(잘안씀)
//    @Autowired private IOC ioc2;
//
//    //2. setter(수정자) 주입 방식(잘안씀)
//    public IOC getIoc(IOC ioc) {
//        ioc2 = ioc;
//        return ioc2;
//    }
//
//    //3. 생성자 주입방식(생성할때 자동으로 주입, 주로 많이 쓰는 방식)
//    public void IOC(IOC ioc){
//        ioc2=ioc;
//    }
    //final 불변성 객체를 변경 할 수 없음
    private final IOC ioc; //개발자가 만든게 아니라 스프링이 객체(bean)를 주입해줬다(Di -> IoC)

    @GetMapping
    public void iocTest() {
        ioc.func1();
    }

}