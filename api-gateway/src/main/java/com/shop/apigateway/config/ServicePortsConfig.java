package com.shop.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "service-ports")
@Getter
@Setter
public class ServicePortsConfig {

    private String productServicePort;
    private String orderServicePort;
    private String inventoryServicePort;
}
