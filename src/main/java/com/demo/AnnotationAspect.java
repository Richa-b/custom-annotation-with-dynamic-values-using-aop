package com.demo;

import com.demo.CustomAnnotation;
import com.demo.CustomSpringExpressionLanguageParser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class AnnotationAspect {

    @Before("methodsAnnotatedWithCustomAnnotation()")
    public void processMethodsAnnotatedWithCustomAnnotation(JoinPoint joinPoint) {
        System.out.println("-> processMethodsAnnotatedWithCustomAnnotation starts");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CustomAnnotation customAnnotation = method.getAnnotation(CustomAnnotation.class);
        Object dynamicValue = CustomSpringExpressionLanguageParser.
                getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), customAnnotation.key());
        System.out.println("Dynamic Value Fetched is:: " + dynamicValue);
    }

    @Pointcut("@annotation(com.demo.CustomAnnotation)")
    private void methodsAnnotatedWithCustomAnnotation() {

    }
}
