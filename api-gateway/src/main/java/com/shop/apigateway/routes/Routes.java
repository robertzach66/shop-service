package com.shop.apigateway.routes;

import com.shop.apigateway.config.RoutingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final RoutingConfig routingConfig;

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return GatewayRouterFunctions.route(routingConfig.getProductRoutes().getName())
                .route(RequestPredicates.path(routingConfig.getProductRoutes().getPath()),
                        HandlerFunctions.http(routingConfig.getProductRoutes().getUrl()))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(routingConfig.getProductRoutes().getCircuitBreaker(),
                        URI.create(routingConfig.getFallbackRoutes().getUrl())))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return GatewayRouterFunctions.route(routingConfig.getProductRoutes().getSwagger().getName())
                .route(RequestPredicates.path(routingConfig.getProductRoutes().getSwagger().getPath()),
                        HandlerFunctions.http(routingConfig.getProductRoutes().getUrl()))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(routingConfig.getProductRoutes().getSwagger().getCircuitBreaker(),
                        URI.create(routingConfig.getFallbackRoutes().getUrl())))
                .filter(setPath(routingConfig.getDocsPath()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route(routingConfig.getOrderRoutes().getName())
                .route(RequestPredicates.POST(routingConfig.getOrderRoutes().getPath()),
                        HandlerFunctions.http(routingConfig.getOrderRoutes().getUrl()))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(routingConfig.getOrderRoutes().getCircuitBreaker(),
                        URI.create(routingConfig.getFallbackRoutes().getUrl())))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return GatewayRouterFunctions.route(routingConfig.getOrderRoutes().getSwagger().getName())
                .route(RequestPredicates.path(routingConfig.getOrderRoutes().getSwagger().getPath()),
                        HandlerFunctions.http(routingConfig.getOrderRoutes().getUrl()))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(routingConfig.getOrderRoutes().getSwagger().getCircuitBreaker(),
                        URI.create(routingConfig.getFallbackRoutes().getUrl())))
                .filter(setPath(routingConfig.getDocsPath()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return GatewayRouterFunctions.route(routingConfig.getInventoryRoutes().getName())
                .route(RequestPredicates.path(routingConfig.getInventoryRoutes().getPath()),
                        HandlerFunctions.http(routingConfig.getInventoryRoutes().getUrl()))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(routingConfig.getInventoryRoutes().getCircuitBreaker(),
                        URI.create(routingConfig.getFallbackRoutes().getUrl())))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        return GatewayRouterFunctions.route(routingConfig.getInventoryRoutes().getSwagger().getName())
                .route(RequestPredicates.path(routingConfig.getInventoryRoutes().getSwagger().getPath()),
                        HandlerFunctions.http(routingConfig.getInventoryRoutes().getUrl()))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(routingConfig.getInventoryRoutes().getSwagger().getCircuitBreaker(),
                        URI.create(routingConfig.getFallbackRoutes().getUrl())))
                .filter(setPath(routingConfig.getDocsPath()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return GatewayRouterFunctions.route(routingConfig.getFallbackRoutes().getName())
                .GET(routingConfig.getFallbackRoutes().getPath(), request -> ServerResponse
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service unavailable. Please try again later."))
                .build();
    }
}
