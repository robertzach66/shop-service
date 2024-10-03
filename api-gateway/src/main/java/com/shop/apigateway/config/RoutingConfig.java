package com.shop.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "routing")
@Getter
@Setter
public class RoutingConfig {

    private String docsPath;
    private List<Route> routes = new ArrayList<>();

    private static Integer PRODUCT_INDEX = 0;
    private static Integer ORDER_INDEX = 1;
    private static Integer INVENTORY_INDEX = 2;
    private static Integer FALLBACK_INDEX = 3;

    @Getter
    @Setter
    public static class Route {
        private String name;
        private String url;
        private String path;
        private String circuitBreaker;
        private Swagger swagger;
    }

    @Getter
    @Setter
    public static class Swagger {
        private String name;
        private String path;
        private String circuitBreaker;
    }

    public Route getProductRoutes() {
        return routes.get(PRODUCT_INDEX);
    }
    public Route getOrderRoutes() {
        return routes.get(ORDER_INDEX);
    }
    public Route getInventoryRoutes() {
        return routes.get(INVENTORY_INDEX);
    }
    public Route getFallbackRoutes() {
        return routes.get(FALLBACK_INDEX);
    }
}
