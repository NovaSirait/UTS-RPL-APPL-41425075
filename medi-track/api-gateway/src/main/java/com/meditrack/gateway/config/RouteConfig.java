package com.meditrack.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://AUTH-SERVICE"))
                .route("appointment-service", r -> r
                        .path("/api/appointments/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://APPOINTMENT-SERVICE"))
                .route("ehr-service", r -> r
                        .path("/api/ehr/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://EHR-SERVICE"))
                .route("pharmacy-service", r -> r
                        .path("/api/pharmacy/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://PHARMACY-SERVICE"))
                .route("analytics-service", r -> r
                        .path("/api/analytics/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://ANALYTICS-SERVICE"))
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://PAYMENT-SERVICE"))
                .build();
    }
}