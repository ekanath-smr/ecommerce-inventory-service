package com.example.ecommerce_inventory_service.clients;

import com.example.ecommerce_inventory_service.exceptions.ProductNotFoundException;
import com.example.ecommerce_inventory_service.exceptions.ProductServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final ProductClient productClient;

    @CircuitBreaker(name = "productService", fallbackMethod = "fallback")
    public void validateProduct(Long productId) {
        log.info("Validating productId={} via product-service", productId);
        productClient.getProductById(productId);
    }

    public void fallback(Long productId, Exception ex) {
        if (ex instanceof FeignException.NotFound) {
            log.warn("Product not found | productId={}", productId);
            throw new ProductNotFoundException(productId);
        } else {
            log.error("Error calling product-service | productId={}", productId, ex);
            throw new ProductServiceUnavailableException("Product service failed for productId: " + productId, ex);
        }
    }
}