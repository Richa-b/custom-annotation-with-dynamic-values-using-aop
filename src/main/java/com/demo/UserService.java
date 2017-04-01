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
