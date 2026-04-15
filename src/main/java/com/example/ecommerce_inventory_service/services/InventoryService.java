package com.example.ecommerce_inventory_service.services;
import com.example.ecommerce_inventory_service.dtos.CreateInventoryRequestDto;
import com.example.ecommerce_inventory_service.dtos.InventoryResponseDto;
import com.example.ecommerce_inventory_service.dtos.StockOperationRequestDto;

public interface InventoryService {

    InventoryResponseDto createInventory(CreateInventoryRequestDto request);

    InventoryResponseDto addStock(Long productId, StockOperationRequestDto request);

    InventoryResponseDto reserveStock(Long productId, StockOperationRequestDto request);

    InventoryResponseDto releaseReservedStock(Long productId, StockOperationRequestDto request);

    InventoryResponseDto confirmSale(Long productId, StockOperationRequestDto request);

    InventoryResponseDto getInventory(Long productId);

    boolean isInStock(Long productId, Integer quantity);
}
