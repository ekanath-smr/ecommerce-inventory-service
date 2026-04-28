package com.example.ecommerce_inventory_service.services;

import com.example.ecommerce_inventory_service.clients.ProductServiceClient;
import com.example.ecommerce_inventory_service.dtos.CreateInventoryRequestDto;
import com.example.ecommerce_inventory_service.dtos.InventoryResponseDto;
import com.example.ecommerce_inventory_service.dtos.StockAvailabilityResponseDto;
import com.example.ecommerce_inventory_service.dtos.StockOperationRequestDto;
import com.example.ecommerce_inventory_service.exceptions.InsufficientStockException;
import com.example.ecommerce_inventory_service.exceptions.InvalidInventoryOperationException;
import com.example.ecommerce_inventory_service.exceptions.InventoryAlreadyExistsException;
import com.example.ecommerce_inventory_service.exceptions.InventoryNotFoundException;
import com.example.ecommerce_inventory_service.mappers.InventoryMapper;
import com.example.ecommerce_inventory_service.models.Inventory;
import com.example.ecommerce_inventory_service.models.InventoryAction;
import com.example.ecommerce_inventory_service.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionService inventoryTransactionService;
    private final ProductServiceClient productServiceClient;

    @Override
    @Transactional
    public InventoryResponseDto createInventory(CreateInventoryRequestDto request) {
        log.info("Creating inventory for productId={}, initialStock={}", request.getProductId(), request.getInitialStock());
        productServiceClient.validateProduct(request.getProductId());
        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new InventoryAlreadyExistsException("Inventory already exist for this productId", request.getProductId());
        }
        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .totalStock(request.getInitialStock())
                .reservedStock(0)
                .soldStock(0)
                .damagedStock(0)
                .build();
        Inventory savedInventory = inventoryRepository.save(inventory);
        inventoryTransactionService.recordTransaction(
                savedInventory.getProductId(),
                "STOCK_ADDED",
                request.getInitialStock(),
                null
        );
        log.info("Inventory created successfully for productId={}", request.getProductId());
        return InventoryMapper.toDto(savedInventory);
    }

    @Override
    @Transactional
    public InventoryResponseDto addStock(Long productId, StockOperationRequestDto request) {
        log.info("Adding stock for productId={}, quantity={}", productId, request.getQuantity());
        Inventory inventory = getInventoryEntity(productId);
        inventory.setTotalStock(inventory.getTotalStock() + request.getQuantity());
        inventoryTransactionService.recordTransaction(
                productId,
                "STOCK_ADDED",
                request.getQuantity(),
                request.getReferenceId()
        );
        log.info("Stock added successfully for productId={}", productId);
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponseDto reserveStock(Long productId, StockOperationRequestDto request) {
        log.info("Reserving stock for productId={}, quantity={}", productId, request.getQuantity());
        Inventory inventory = getInventoryEntity(productId);
        int availableStock = calculateAvailableStock(inventory);
        if (availableStock < request.getQuantity()) {
            log.error("Insufficient stock for productId={}, requested={}, available={}", productId, request.getQuantity(), availableStock);
            throw new InsufficientStockException("Insufficient stock", productId);
        }
        inventory.setReservedStock(inventory.getReservedStock() + request.getQuantity());
        inventoryTransactionService.recordTransaction(
                productId,
                "STOCK_RESERVED",
                request.getQuantity(),
                request.getReferenceId()
        );
        log.info("Stock reserved successfully for productId={}", productId);
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponseDto releaseReservedStock(Long productId, StockOperationRequestDto request) {
        log.info("Releasing reserved stock for productId={}, quantity={}", productId, request.getQuantity());
        Inventory inventory = getInventoryEntity(productId);
        if (inventory.getReservedStock() < request.getQuantity()) {
            log.error("Invalid release request for productId={}, reserved={}, requested={}", productId, inventory.getReservedStock(), request.getQuantity());
            throw new InvalidInventoryOperationException("Cannot release more reserved stock than currently reserved", productId);
        }
        inventory.setReservedStock(inventory.getReservedStock() - request.getQuantity());
        inventoryTransactionService.recordTransaction(
                productId,
                "STOCK_RELEASED",
                request.getQuantity(),
                request.getReferenceId()
        );
        log.info("Reserved stock released successfully for productId={}", productId);
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponseDto confirmSale(Long productId, StockOperationRequestDto request) {
        log.info("Confirming sale for productId={}, quantity={}", productId, request.getQuantity());
        Inventory inventory = getInventoryEntity(productId);
        if (inventory.getReservedStock() < request.getQuantity()) {
            log.error("Invalid confirm sale for productId={}, reserved={}, requested={}", productId, inventory.getReservedStock(), request.getQuantity());
            throw new InvalidInventoryOperationException("Cannot confirm sale for more than reserved stock", productId);
        }
        inventory.setReservedStock(inventory.getReservedStock() - request.getQuantity());
        inventory.setSoldStock(inventory.getSoldStock() + request.getQuantity());
        inventoryTransactionService.recordTransaction(
                productId,
                "SALE_CONFIRMED",
                request.getQuantity(),
                request.getReferenceId()
        );
        log.info("Sale confirmed successfully for productId={}", productId);
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponseDto undoConfirmedSale(Long productId, StockOperationRequestDto request) {
        log.warn("Undoing confirmed sale for productId={}, quantity={}", productId, request.getQuantity());
        int quantity = request.getQuantity();
        Inventory inventory = getInventoryEntity(productId);
        if (inventory.getSoldStock() < quantity) {
            log.error("Invalid undo sale for productId={}, sold={}, requested={}", productId, inventory.getSoldStock(), quantity);
            throw new InvalidInventoryOperationException("Cannot undo sale. Sold stock is less than requested rollback quantity", productId);
        }
        inventory.setSoldStock(inventory.getSoldStock() - quantity);
        inventory.setReservedStock(inventory.getReservedStock() + quantity);
        inventoryTransactionService.recordTransaction(
                productId,
                "UNDO_SALE",
                quantity,
                request.getReferenceId()
        );
        log.info("Undo sale successful for productId={}", productId);
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponseDto getInventory(Long productId) {
        log.info("Fetching inventory for productId={}", productId);
        return InventoryMapper.toDto(getInventoryEntity(productId));
    }

    @Override
    public boolean isInStock(Long productId, Integer quantity) {
        log.info("Checking stock availability for productId={}, quantity={}", productId, quantity);
        Inventory inventory;
        int availableStock;
        try {
            inventory = getInventoryEntity(productId);
            availableStock = calculateAvailableStock(inventory);
        } catch (InventoryNotFoundException ex) {
            log.warn("Inventory not found for productId={}", productId);
            availableStock = 0;
        }
        return availableStock >= quantity;
    }

    @Override
    public StockAvailabilityResponseDto getAvailableStock(Long productId) {
        log.info("Getting available stock for productId={}", productId);
        Inventory inventory;
        int availableStock;
        try {
            inventory = getInventoryEntity(productId);
            availableStock = calculateAvailableStock(inventory);
        } catch (InventoryNotFoundException ex) {
            log.warn("Inventory not found for productId={}", productId);
            availableStock = 0;
        }
        return StockAvailabilityResponseDto.builder()
                .productId(productId)
                .availableStock(availableStock)
                .build();
    }

    private Inventory getInventoryEntity(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.error("Inventory not found for productId={}", productId);
                    return new InventoryNotFoundException(
                            "Inventory not found for productId: " + productId,
                            productId
                    );
                });
    }

    private int calculateAvailableStock(Inventory inventory) {
        if(inventory == null) {
            return 0;
        }
        return inventory.getTotalStock() - inventory.getReservedStock()
                        - inventory.getSoldStock() - inventory.getDamagedStock();
    }
}

//ProductService = source of truth
//InventoryService validates via API call
//Handles failures with exceptions
//Uses circuit breaker for resilience

//“We initially used synchronous REST calls for product validation to ensure strong consistency.
// For scalability, we can evolve to an event-driven model using Kafka for product sync, but now prioritized correctness
// and simplicity in the first version.”

//“I added Resilience4j circuit breaker and retry to prevent cascading failures between Inventory
// and Product microservices”