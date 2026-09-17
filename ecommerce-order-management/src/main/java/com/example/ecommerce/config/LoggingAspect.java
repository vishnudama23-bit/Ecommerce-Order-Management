package com.example.ecommerce.config;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(LoggingAspect.class);

    private final HttpServletRequest request;

    public LoggingAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Around("execution(* com.example.ecommerce.controller..*(..))")
    public Object logControllerMethods(
            ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName =
                joinPoint.getSignature().getName();

        String className =
                joinPoint.getTarget().getClass().getSimpleName();

        String httpMethod =
                request.getMethod();

        String requestUri =
                request.getRequestURI();

        logger.info(
                "API Request: {} {} → {}.{}()",
                httpMethod,
                requestUri,
                className,
                methodName);

        long startTime =
                System.currentTimeMillis();

        Object result =
                joinPoint.proceed();

        long executionTime =
                System.currentTimeMillis() - startTime;

        logger.info(
                "API Response: {}.{}() completed in {} ms",
                className,
                methodName,
                executionTime);

        return result;
    }
}