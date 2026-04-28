package com.example.ecommerce_inventory_service.advices;

import com.example.ecommerce_inventory_service.dtos.ErrorResponseDto;
import com.example.ecommerce_inventory_service.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleInventoryNotFound(InventoryNotFoundException ex, HttpServletRequest request) {
        log.warn("Inventory not found | method={} | path={} | productId={} | message={}",
                request.getMethod(), request.getRequestURI(), ex.getProductId(), ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not allowed | method={} | path={}", ex.getMethod(), request.getRequestURI());
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED,
                "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint"
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest request) {
        log.warn("Invalid productId | path={} | message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Invalid productId, product not found");
    }

    @ExceptionHandler(ProductServiceUnavailableException.class)
    public ResponseEntity<ErrorResponseDto> handleProductServiceUnavailable(ProductServiceUnavailableException ex, HttpServletRequest request) {
        log.error("Product service unavailable | path={} | message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Product Service Not Available");
    }

    @ExceptionHandler(InventoryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleInventoryAlreadyExists(InventoryAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Inventory already exists | productId={} | path={}", ex.getProductId(), request.getRequestURI());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponseDto> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        log.warn("Insufficient stock | productId={} | path={} | message={}", ex.getProductId(), request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidInventoryOperationException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidOperation(InvalidInventoryOperationException ex, HttpServletRequest request) {
        log.warn("Invalid inventory operation | productId={} | path={} | message={}", ex.getProductId(), request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InventoryActionNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleActionNotFound(InventoryActionNotFoundException ex, HttpServletRequest request) {
        log.warn("Inventory action not found | path={} | message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorizationDenied(AuthorizationDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied | path={}", request.getRequestURI());
        return buildErrorResponse(HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource"
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failed | path={} | message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                "Authentication failed"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception | method={} | path={} | message={}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
}