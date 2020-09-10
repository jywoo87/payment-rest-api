package com.jywoo.payment.lock.aop;

import com.jywoo.payment.except.ConcurrencyException;
import com.jywoo.payment.lock.annotations.OperationLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.stream.IntStream;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LockAspect {
    private final SpelExpressionParser expressionParser = new SpelExpressionParser();
    private final LockRegistry lockRegistry;

    @Pointcut("execution(* com.jywoo.payment.usecase..*(..)) && @annotation(operationLock))")
    public void OperationLockPointCut(OperationLock operationLock) {
    }

    @Before("OperationLockPointCut(operationLock)")
    public void beforeLockAop(JoinPoint joinPoint, OperationLock operationLock) {
        final Object findObject = getValue(joinPoint, operationLock.expression());
        if(findObject == null) {
            return;
        }

        final String value = String.valueOf(findObject);
        final Lock lock = lockRegistry.obtain(value);
        if(!lock.tryLock()) {
            throw new ConcurrencyException(operationLock.exceptionMessage());
        }
        log.debug("-- lock : {}, {}", operationLock.operation(), value);
    }

    @After("OperationLockPointCut(operationLock)")
    public void afterLockAop(JoinPoint joinPoint, OperationLock operationLock) {
        final Object findObject = getValue(joinPoint, operationLock.expression());
        if(findObject == null) {
            return;
        }
        final String value = String.valueOf(findObject);

        final Lock lock = lockRegistry.obtain(value);
        log.debug("-- unlock request : {}, {}", operationLock.operation(), value);
        lock.unlock();
        log.debug("-- unlock success : {}, {}", operationLock.operation(), value);
    }

    public Object getValue(JoinPoint joinPoint, String spelExpression) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final String[] parameterNames = methodSignature.getParameterNames();
        final Object[] args = joinPoint.getArgs();

        final EvaluationContext context = new StandardEvaluationContext();
        IntStream.range(0, parameterNames.length).forEach(i -> context.setVariable(parameterNames[i], args[i]));

        final Expression expression = expressionParser.parseExpression(spelExpression);
        return expression.getValue(context);
    }
}
