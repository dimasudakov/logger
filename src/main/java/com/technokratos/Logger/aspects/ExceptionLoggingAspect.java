package com.technokratos.Logger.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;

@Aspect
@Slf4j
public class ExceptionLoggingAspect {

    private static final String AFTER_THROWING_MESSAGE_PATTERN =
            "Request ID: {} - Exception occurred in method {} in {} with args {} with exception message: {}";

    @Pointcut("within(com.technokratos..*)")
    public void loggableMethods() {}

    @AfterThrowing(pointcut = "loggableMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String requestId = MDC.get("requestId");
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        String classOfMethod = joinPoint.getTarget().getClass().getSimpleName();

        log.error(AFTER_THROWING_MESSAGE_PATTERN,
                requestId,
                methodName,
                classOfMethod,
                args,
                ex.getMessage(),
                ex
        );
    }
}
