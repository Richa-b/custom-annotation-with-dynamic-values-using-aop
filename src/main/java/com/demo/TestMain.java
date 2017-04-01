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
