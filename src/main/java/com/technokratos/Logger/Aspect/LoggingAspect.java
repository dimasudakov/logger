package com.technokratos.Logger.Aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final String USER_REQUEST_ID_KEY = "request_id";

    private static final String BEFORE_METHOD_MESSAGE_PATTERN =
            "Request ID: {} - Method {} in {} called with args {}";

    private static final String SUCCESS_METHOD_MESSAGE_PATTERN =
            "Request ID: {} - Method {} in {} executed successfully in {} ms with result {}";

    private static final String AFTER_THROWING_MESSAGE_PATTERN =
            "Request ID: {} - Exception occurred in method {} in {} with args {} with exception message: {}";

    @Pointcut("@annotation(com.technokratos.Logger.annotation.LogMethodExecution)")
    public void logMethodCall() {}

    @Around("@annotation(com.technokratos.Logger.annotation.LogMethodExecution)")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = (String) Optional.ofNullable(MDC.get(USER_REQUEST_ID_KEY))
                .orElse(UUID.randomUUID().toString());
        MDC.put(USER_REQUEST_ID_KEY, requestId);

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
                    e.getMessage(),
                    e
            );
            throw e;
        }
    }


    @Pointcut("execution(* com.technokratos.controllers.*Controller.*(..))")
    private void controllerPointcut() {}

    @Before("controllerPointcut()")
    public void logBeforeControllerMethods(JoinPoint joinPoint) {
        String requestId = Optional.ofNullable(org.slf4j.MDC.get(USER_REQUEST_ID_KEY))
                .orElse(UUID.randomUUID().toString());
        MDC.put(USER_REQUEST_ID_KEY, requestId);

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


    @Pointcut("within(com.technokratos..*)")
    public void loggableMethods() {}

    @AfterThrowing(pointcut = "loggableMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String requestId = Optional.ofNullable(org.slf4j.MDC.get(USER_REQUEST_ID_KEY))
                .orElse(UUID.randomUUID().toString());
        MDC.put(USER_REQUEST_ID_KEY, requestId);

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
