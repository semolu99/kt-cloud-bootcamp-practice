package com.example.my_api_server;

import org.springframework.stereotype.Component;

//실제 스프링에게 빈(객체)으로 등록하게 해주는 설정
//IOC 컨테이너에 등록됩니다. (객체=물건, 단 하나만 생성이됩니다. 싱글톤 패턴)
@Component
public class IOC {
    //@Bean 매서드 단위로 등록, 매서드의 리턴 타입의 객체를 등록해줌
    //직접 제어하는 명시적 등록 방법, 주로 커스텀이나 외부라이브러리 등록시 사용
    public void func1() {
        System.out.println("IOC.func1 실행");
    }

    static void main(String[] args) {
        //객체 생성
        //메모리(RAM), JVM Heap 메모리에 사용한다.
        //OOM (Out of Heap memory)

        //spring한테 IOC라는 객체를 만들어줄테니, 대신 하나로만 만들어서 재사용하게 해줘(IOC)?
        //개발자가 직접 만드는게 아니라, 스프링 프레임워크가 관리해주는것 필요할때 스프링이 주입해준다(DI)
        IOC ioc = new IOC();

        ioc.func1();
    }
}
