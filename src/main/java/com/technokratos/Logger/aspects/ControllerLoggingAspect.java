package com.technokratos.Logger.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    private static final String BEFORE_METHOD_MESSAGE_PATTERN =
            "Request ID: {} - Method {} in {} called with args {}";

    @Pointcut("execution(* com.technokratos.controllers.*Controller.*(..))")
    private void controllerPointcut() {}

    @Before("controllerPointcut()")
    public void logBeforeControllerMethods(JoinPoint joinPoint) {
        String requestId = Optional.ofNullable(MDC.get("requestID"))
                .orElse(UUID.randomUUID().toString());

        MDC.put("requestId", requestId);

        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        String classOfMethod = joinPoint.getTarget().getClass().getSimpleName();

        log.info(BEFORE_METHOD_MESSAGE_PATTERN,
                requestId,
                methodName,
                classOfMethod,
                args
        );
    }
}
