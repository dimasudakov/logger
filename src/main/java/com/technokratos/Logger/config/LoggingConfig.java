package com.technokratos.Logger.config;

import com.technokratos.Logger.aspects.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@Configuration
@EnableAspectJAutoProxy
public class LoggingConfig {

    @Bean
    public AnnotationLoggingAspect annotationLoggingAspect() {
        return new AnnotationLoggingAspect();
    }

    @Bean
    public ControllerLoggingAspect controllerLoggingAspect() {
        return new ControllerLoggingAspect();
    }

    @Bean
    public ExceptionLoggingAspect exceptionLoggingAspect() {
        return new ExceptionLoggingAspect();
    }

}
