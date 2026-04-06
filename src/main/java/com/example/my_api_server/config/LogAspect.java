package com.example.my_api_server.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogAspect {

    //메서드 실행시 시간 찍기
    @Around("execution(* com.example.my_api_server.service..*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            //log.info(joinPoint.getSignature() + " 실행 시간 : "+(endTime - startTime)+"ms"
            log.info("{} 실행 시간 : {}ms", joinPoint.getSignature(), endTime - startTime);
            //실무에서는 AOP 로길
            //운영환경에서 모든 로그 수집하지 않고 특정 필요한 로그를 AOP에서 ES(ELK)
        }
    }
}
