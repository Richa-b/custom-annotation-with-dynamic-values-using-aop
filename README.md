# custom-annotation-with-dynamic-values-using-aop
Getting Dynamic Values From method Parameters in Custom Annotations using Spring AOP

# Requirements
Dependency for spring core and spring aop

### Implementation

- ### Directory Structure
![img](http://i66.tinypic.com/23sjl0l.png)

- ### pom.xml
```sh
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.demo</groupId>
    <artifactId>SpringAOP</artifactId>
    <packaging>jar</packaging>
    <version>1.0-SNAPSHOT</version>
    <name>SpringAOP</name>
    <url>http://maven.apache.org</url>

    <properties>
        <spring.version>4.1.1.RELEASE</spring.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjrt</artifactId>
            <version>1.6.11</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.6.11</version>
        </dependency>
    </dependencies>

    <build>
        <finalName>SpringApp</finalName>
        <plugins>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.6</source>
                    <target>1.6</target>
                </configuration>
            </plugin>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <includes>
                        <include>**/*Tests.java</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
This project requires dependencies for spring-core and spring-aop to be included.
- ### spring-config.xml
```sh
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">

    <context:annotation-config/>
    <context:component-scan base-package="com.demo"/>
    <aop:aspectj-autoproxy/>
</beans>
```

- ### CustomAnnotation
```sh
package com.demo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CustomAnnotation {

    /**
     * Spring Expression Language (SpEL) attribute for computing the key dynamically.
     */
    String key();
}
```
"key" in this annotation will contain the parameters which will be calculated dynamically.

- ### User.java
```sh
package com.demo;
public class User {
    private String firstName;
    private String lastName;
    private Long age;
    private Address address;
    private Long id;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User(String firstName, String lastName, Long age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public User(Address address) {
        this.address = address;
    }
}

```

- ### Address.groovy
```sh
package com.demo;
public class Address {

    private String city;
    private String state;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Address(String city, String state) {
        this.city = city;
        this.state = state;
    }
}
```

- ### AnnotationAspect
```sh
package com.demo;

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
```

- ### CustomSpringExpressionLanguageParser
```sh
package com.demo;


import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringExpressionLanguageParser {
    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return (Object) parser.parseExpression(key).getValue(context, Object.class);
    }
}
```
This is a custom Spring EL parser that takes key whise value is to be found, value array of arguments and array of parameter names.  

- ### UserService.java
```sh
package com.demo;

import org.springframework.stereotype.Component;

@Component
public class UserService {

    @CustomAnnotation(key = "#firstName")
    public void getUserFirstName(String firstName) {
        System.out.println("getUserFirstName");
        // business logic
    }

    @CustomAnnotation(key = "#user.age")
    public void getUserAge(User user) {
        System.out.println("getUserAge");
        // business logic
    }

    @CustomAnnotation(key = "#user.address.city")
    public void getUserCity(User user) {
        System.out.println("getUserCity");
        // business logic
    }
}

```

This service contains test methods which take key as flat String ("#firstName"), nested fields (#user.age)  and fields inside nested objects ("#user.address.city)

- ### TestMain.java
```sh
package com.demo;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestMain {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        UserService userService = context.getBean(UserService.class);
        userService.getUserFirstName("Dummy User");
        userService.getUserAge(new User("Dummy", "User", 30l));
        userService.getUserCity(new User(new Address("Noida", "UP")));
    }
}
```

### Output

```sh
-> processMethodsAnnotatedWithCustomAnnotation starts
Dynamic Value Fetched is:: Dummy User
getUserFirstName
-> processMethodsAnnotatedWithCustomAnnotation starts
Dynamic Value Fetched is:: 30
getUserAge
-> processMethodsAnnotatedWithCustomAnnotation starts
Dynamic Value Fetched is:: Noida
getUserCity
```
