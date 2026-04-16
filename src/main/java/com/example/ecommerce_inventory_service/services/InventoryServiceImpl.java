package com.example.ecommerce_inventory_service.services;

import com.example.ecommerce_inventory_service.dtos.CreateInventoryRequestDto;
import com.example.ecommerce_inventory_service.dtos.InventoryResponseDto;
import com.example.ecommerce_inventory_service.dtos.StockOperationRequestDto;
import com.example.ecommerce_inventory_service.exceptions.InsufficientStockException;
import com.example.ecommerce_inventory_service.exceptions.InvalidInventoryOperationException;
import com.example.ecommerce_inventory_service.exceptions.InventoryAlreadyExistsException;
import com.example.ecommerce_inventory_service.exceptions.InventoryNotFoundException;
import com.example.ecommerce_inventory_service.mappers.InventoryMapper;
import com.example.ecommerce_inventory_service.models.Inventory;
import com.example.ecommerce_inventory_service.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: need to verify if the product exists by calling productService.

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionService inventoryTransactionService;

    @Override
    @Transactional
    public InventoryResponseDto createInventory(CreateInventoryRequestDto request) {
        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new InventoryAlreadyExistsException("Inventory already exist", request.getProductId());
        }
        Inventory inventory = new Inventory();
        inventory.setProductId(request.getProductId());
        inventory.setTotalStock(request.getInitialStock());
        inventory.setReservedStock(0);
        inventory.setSoldStock(0);
        inventory.setDamagedStock(0);
        Inventory savedInventory = inventoryRepository.save(inventory);
        inventoryTransactionService.recordTransaction(
                savedInventory.getProductId(),
                "STOCK_ADDED",
                request.getInitialStock(),
                null
        );
        return InventoryMapper.toDto(savedInventory);
    }

    @Override
    @Transactional
    public InventoryResponseDto addStock(Long productId, StockOperationRequestDto request) {
        Inventory inventory = getInventoryEntity(productId);
        inventory.setTotalStock(inventory.getTotalStock() + request.getQuantity());
        inventoryTransactionService.recordTransaction(
                productId,
                "STOCK_ADDED",
                request.getQuantity(),
                request.getReferenceId()
        );
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponseDto reserveStock(Long productId, StockOperationRequestDto request) {
        Inventory inventory = getInventoryEntity(productId);
        int availableStock = calculateAvailableStock(inventory);
        if (availableStock < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock", productId);
        }
        inventory.setReservedStock(inventory.getReservedStock() + request.getQuantity());
        inventoryTransactionService.recordTransaction(
                productId,
                "STOCK_RESERVED",
                request.getQuantity(),
                request.getReferenceId()
        );
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponseDto releaseReservedStock(Long productId, StockOperationRequestDto request) {
        Inventory inventory = getInventoryEntity(productId);
        if (inventory.getReservedStock() < request.getQuantity()) {
            throw new InvalidInventoryOperationException("Cannot release more reserved stock than currently reserved");
        }
        inventory.setReservedStock(inventory.getReservedStock() - request.getQuantity());
        inventoryTransactionService.recordTransaction(
                productId,
                "STOCK_RELEASED",
                request.getQuantity(),
                request.getReferenceId()
        );
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponseDto confirmSale(Long productId, StockOperationRequestDto request) {
        Inventory inventory = getInventoryEntity(productId);
        if (inventory.getReservedStock() < request.getQuantity()) {
            throw new InvalidInventoryOperationException("Cannot confirm sale for more than reserved stock");
        }
        inventory.setReservedStock(inventory.getReservedStock() - request.getQuantity());
        inventory.setSoldStock(inventory.getSoldStock() + request.getQuantity());
        inventoryTransactionService.recordTransaction(
                productId,
                "SALE_CONFIRMED",
                request.getQuantity(),
                request.getReferenceId()
        );
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponseDto getInventory(Long productId) {
        return InventoryMapper.toDto(getInventoryEntity(productId));
    }

    @Override
    public boolean isInStock(Long productId, Integer quantity) {
        Inventory inventory = getInventoryEntity(productId);
        return calculateAvailableStock(inventory) >= quantity;
    }

    private Inventory getInventoryEntity(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for this productId", productId));
    }

    private int calculateAvailableStock(Inventory inventory) {
        return inventory.getTotalStock() - inventory.getReservedStock()
                        - inventory.getSoldStock() - inventory.getDamagedStock();
    }
}