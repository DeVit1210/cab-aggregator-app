package com.modsen.payment.advice;

import com.modsen.payment.exception.CustomStripeException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.UndeclaredThrowableException;

@Aspect
@Slf4j
@Component
public class StripeServiceAdvice {
    private static final String PK_KEY = "PK_KEY";
    private static final String SK_KEY = "SK_KEY";

    @Pointcut("execution(public * com.modsen.payment.service.StripeService.*(..))")
    public void callAtStripeServiceMethod() {
    }

    @AfterThrowing(pointcut = "callAtStripeServiceMethod()", throwing = "e")
    public void afterReturningFromAnyStripeServiceMethod(JoinPoint joinPoint, Throwable e) {
        if (e.getClass().equals(UndeclaredThrowableException.class)) {
            Throwable target = e.getCause();
            if (StripeException.class.isAssignableFrom(target.getClass())) {
                StripeException stripeException = (StripeException) target;
                log.info("Method {} threw {} exception", joinPoint.getSignature().getName(), stripeException.getMessage());
                throw new CustomStripeException(stripeException.getCode());
            }
        }

        log.info("Method {} threw {} exception", joinPoint.getSignature().getName(), e.getMessage());
    }

    @Pointcut("execution(public * com.modsen.payment.service.StripeService.*(..)) && @annotation(PublishableKey)")
    public void callAtPublishableKeyAnnotatedMethod() {
    }

    @Before("callAtPublishableKeyAnnotatedMethod()")
    public void beforeCallingPublishableKeyAnnotatedMethod() {
        Stripe.apiKey = System.getenv(PK_KEY);
    }

    @After("callAtPublishableKeyAnnotatedMethod()")
    public void afterCallingPublishableKeyAnnotatedMethod() {
        Stripe.apiKey = System.getenv(SK_KEY);
    }
}
