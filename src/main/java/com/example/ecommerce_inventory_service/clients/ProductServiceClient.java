package com.example.ecommerce_inventory_service.clients;

import com.example.ecommerce_inventory_service.exceptions.ProductNotFoundException;
import com.example.ecommerce_inventory_service.exceptions.ProductServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceClient {

    private final ProductClient productClient;

    public ProductServiceClient(ProductClient productClient) {
        this.productClient = productClient;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "fallback")
    public void validateProduct(Long productId) {
        productClient.getProductById(productId);
    }

    public void fallback(Long productId, Exception ex) {
        if (ex instanceof feign.FeignException.NotFound) {
            throw new ProductNotFoundException(productId);
        }
        throw new ProductServiceUnavailableException(
                "Product service is unavailable for productId: " + productId,
                ex
        );
    }
}
