package com.modsen.payment.advice;

import com.modsen.payment.exception.CustomStripeException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.UndeclaredThrowableException;

@Aspect
@Slf4j
@Component
public class StripeServiceAdvice {
    @Value("${stripe.key.publishable}")
    private String PK_KEY;
    @Value("${stripe.key.secret}")
    private String SK_KEY;
    @Pointcut("execution(public * com.modsen.payment.service.StripeService.*(..))")
    public void callAtStripeServiceMethod() { }

    @AfterThrowing(pointcut = "callAtStripeServiceMethod()", throwing = "e")
    public void afterReturningFromAnyStripeServiceMethod(JoinPoint joinPoint, Throwable e) {
        if(e.getClass().equals(UndeclaredThrowableException.class)) {
            Throwable target = e.getCause();
            if(StripeException.class.isAssignableFrom(target.getClass())) {
                StripeException stripeException = (StripeException) target;
                log.info("Method {} threw {} exception", joinPoint.getSignature().getName(), stripeException.getMessage());
                throw new CustomStripeException(stripeException.getCode());
            }
        }

        log.info("Method {} threw {} exception", joinPoint.getSignature().getName(), e.getMessage());
    }

    @Pointcut("execution(public * com.modsen.payment.service.StripeService.*(..)) && @annotation(PublishableKey)")
    public void callAtPublishableKeyAnnotatedMethod() { }

    @Before("callAtPublishableKeyAnnotatedMethod()")
    public void beforeCallingPublishableKeyAnnotatedMethod(JoinPoint joinPoint) {
        Stripe.apiKey = PK_KEY;
    }

    @After("callAtPublishableKeyAnnotatedMethod()")
    public void afterCallingPublishableKeyAnnotatedMethod(JoinPoint joinPoint) {
        Stripe.apiKey = SK_KEY;
    }
}
