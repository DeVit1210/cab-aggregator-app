package com.modsen.ride.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Slf4j
@Component
public class LoggingAdvice {
    @Pointcut("within(com.modsen.ride.service..*)")
    public void servicePointcut() {
    }

    @Pointcut("within(com.modsen.ride.kafka.consumer..*)")
    public void kafkaConsumerPointcut() {
    }

    @Pointcut("within(com.modsen.ride.kafka.producer..*)")
    public void kafkaProducerPointcut() {
    }

    @Before("servicePointcut()")
    public void logMethodInvocation(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.info("Method {}.{} called with args {}", getClassName(joinPoint), getMethodName(joinPoint), Arrays.toString(args));
    }

    @Before("kafkaConsumerPointcut()")
    public void logKafkaConsumerMethodInvocation(JoinPoint joinPoint) {
        Object record = joinPoint.getArgs()[0];
        String topicName = getTopicName(joinPoint);
        log.info("Record {} consumed from topic {}", record, topicName);
    }

    @Before("kafkaProducerPointcut()")
    public void logKafkaProducerMethodInvocation(JoinPoint joinPoint) {
        Object record = joinPoint.getArgs()[0];
        log.info("Record {} is produced", record);
    }

    @AfterReturning(pointcut = "servicePointcut()", returning = "result")
    public void logMethodExecutionResult(JoinPoint joinPoint, Object result) {
        log.info("Method {}.{} returned {}", getClassName(joinPoint), getMethodName(joinPoint), result);
    }

    @AfterThrowing(pointcut = "servicePointcut() || kafkaProducerPointcut() || kafkaConsumerPointcut()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        log.error("Method {}.{} threw an exception {} ", getClassName(joinPoint), getMethodName(joinPoint), exception.getMessage());
    }

    private String getClassName(JoinPoint joinPoint) {
        return Introspector.decapitalize(
                joinPoint.getSignature()
                        .getDeclaringType()
                        .getSimpleName()
        );
    }

    private String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    private String getTopicName(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        KafkaListener kafkaListenerAnnotation = targetMethod.getAnnotation(KafkaListener.class);
        return kafkaListenerAnnotation.topics()[0];
    }
}
