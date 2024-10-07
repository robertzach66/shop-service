package com.shop.notification;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.from(NotificationApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}