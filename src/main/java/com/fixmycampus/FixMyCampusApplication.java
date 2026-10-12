package com.fixmycampus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// We do our own login with JWT, so Spring's default in-memory user is switched off.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class FixMyCampusApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixMyCampusApplication.class, args);
    }
}
