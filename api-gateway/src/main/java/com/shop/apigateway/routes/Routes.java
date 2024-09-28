package com.shop.apigateway.routes;

import com.shop.apigateway.config.ServicePortsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class Routes {

    private static final String FALLBACK_URL = "/fallbackRoute";
    private static final String FALLBACK_URI = "forward:" + FALLBACK_URL;
    private static final String API_DOCS_URL = "/api-docs";

    @Value("${server.url}")
    private String serverUrl;

    private final ServicePortsConfig servicePortsConfig;

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return GatewayRouterFunctions.route("product_service")
                .route(RequestPredicates.path("/api/product"),
                        HandlerFunctions.http(buildUrl(serverUrl, servicePortsConfig.getProductServicePort())))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceCircuitBreake",
                        URI.create(FALLBACK_URI)))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("product_service_swagger")
                .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"),
                        HandlerFunctions.http(buildUrl(serverUrl, servicePortsConfig.getProductServicePort())))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceSwaggerCircuitBreake",
                        URI.create(FALLBACK_URI)))
                .filter(setPath(API_DOCS_URL))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route("order_service")
                .route(RequestPredicates.POST("/api/order"),
                        HandlerFunctions.http(buildUrl(serverUrl, servicePortsConfig.getOrderServicePort())))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCircuitBreake",
                        URI.create(FALLBACK_URI)))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderCreateServiceRoute() {
        return GatewayRouterFunctions.route("order_service")
                .route(RequestPredicates.POST("/api/order/create"),
                        HandlerFunctions.http(buildUrl(serverUrl, servicePortsConfig.getOrderServicePort())))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCreateCircuitBreake",
                        URI.create(FALLBACK_URI)))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("order_service_swagger")
                .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"),
                        HandlerFunctions.http(buildUrl(serverUrl, servicePortsConfig.getOrderServicePort())))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceSwaggerCircuitBreake",
                        URI.create(FALLBACK_URI)))
                .filter(setPath(API_DOCS_URL))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return GatewayRouterFunctions.route("inventory_service")
                .route(RequestPredicates.path("/api/inventory"),
                        HandlerFunctions.http(buildUrl(serverUrl, servicePortsConfig.getInventoryServicePort())))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreake",
                        URI.create(FALLBACK_URI)))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("inventory_service_swagger")
                .route(RequestPredicates.path("/aggregate/inventory-service/v3/api-docs"),
                        HandlerFunctions.http(buildUrl(serverUrl, servicePortsConfig.getInventoryServicePort())))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceSwaggerCircuitBreake",
                        URI.create(FALLBACK_URI)))
                .filter(setPath(API_DOCS_URL))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return GatewayRouterFunctions.route("fallbackRoute")
                .GET(FALLBACK_URL, request -> ServerResponse
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service unavailable. Please try again later."))
                .build();
    }

    private String buildUrl(final String url, final String port) {
        return url + ":" + port;
    }
}
