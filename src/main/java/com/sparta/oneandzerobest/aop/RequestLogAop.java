package com.sparta.oneandzerobest.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "RequestLogAop")
@Aspect
@Component
public class RequestLogAop {
    @Pointcut("execution(* com.sparta.oneandzerobest.*.controller.*.*(..))")
    private void allControllers() {}

    @Before("allControllers()")
    public void requestLog() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.info("Request URL: {}, HTTP Method: {}", url, method);
    }
}
