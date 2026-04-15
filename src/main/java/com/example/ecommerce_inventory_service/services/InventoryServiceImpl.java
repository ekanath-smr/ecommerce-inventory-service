package com.example.ecommerce_inventory_service.services;
import com.example.ecommerce_inventory_service.dtos.CreateInventoryRequestDto;
import com.example.ecommerce_inventory_service.dtos.InventoryResponseDto;
import com.example.ecommerce_inventory_service.dtos.StockOperationRequestDto;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Override
    public InventoryResponseDto createInventory(CreateInventoryRequestDto request) {
        return null;
    }

    @Override
    public InventoryResponseDto addStock(Long productId, StockOperationRequestDto request) {
        return null;
    }

    @Override
    public InventoryResponseDto reserveStock(Long productId, StockOperationRequestDto request) {
        return null;
    }

    @Override
    public InventoryResponseDto releaseReservedStock(Long productId, StockOperationRequestDto request) {
        return null;
    }

    @Override
    public InventoryResponseDto confirmSale(Long productId, StockOperationRequestDto request) {
        return null;
    }

    @Override
    public InventoryResponseDto getInventory(Long productId) {
        return null;
    }

    @Override
    public boolean isInStock(Long productId, Integer quantity) {
        return false;
    }
}
