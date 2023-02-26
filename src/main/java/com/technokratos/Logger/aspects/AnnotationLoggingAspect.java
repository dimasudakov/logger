package com.technokratos.Logger.aspects;

import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class AnnotationLoggingAspect {

    private static final String BEFORE_METHOD_MESSAGE_PATTERN =
            "Request ID: {} - Method {} in {} called with args {}";

    private static final String SUCCESS_METHOD_MESSAGE_PATTERN =
            "Request ID: {} - Method {} in {} executed successfully in {} ms with result {}";

    private static final String AFTER_THROWING_MESSAGE_PATTERN =
            "Request ID: {} - Exception occurred while executing method {} in {} with args {}";

    @Pointcut("@annotation(com.technokratos.Logger.annotation.LogMethodExecution)")
    public void logMethodCall() {}

    @Around("@annotation(com.technokratos.Logger.annotation.LogMethodExecution)")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = (String) Optional.ofNullable(MDC.get("requestID"))
                .orElse(UUID.randomUUID().toString());
        MDC.put("requestId", requestId);

        Object[] args = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String classOfMethod = joinPoint.getTarget().getClass().getSimpleName();

        log.info(BEFORE_METHOD_MESSAGE_PATTERN,
                requestId,
                methodName,
                classOfMethod,
                args
        );

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.info(SUCCESS_METHOD_MESSAGE_PATTERN,
                        requestId,
                        methodName,
                        classOfMethod,
                        executionTime,
                        result
            );
            return result;
        } catch (Exception e) {

            log.error(AFTER_THROWING_MESSAGE_PATTERN,
                    requestId,
                    methodName,
                    classOfMethod,
                    args,
                    e
            );
            throw e;
        }
    }
}
