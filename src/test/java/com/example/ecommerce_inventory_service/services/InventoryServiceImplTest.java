package com.example.ecommerce_inventory_service.services;

import com.example.ecommerce_inventory_service.clients.ProductServiceClient;
import com.example.ecommerce_inventory_service.dtos.CreateInventoryRequestDto;
import com.example.ecommerce_inventory_service.dtos.InventoryResponseDto;
import com.example.ecommerce_inventory_service.dtos.StockOperationRequestDto;
import com.example.ecommerce_inventory_service.exceptions.*;
import com.example.ecommerce_inventory_service.models.Inventory;
import com.example.ecommerce_inventory_service.repositories.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryTransactionService inventoryTransactionService;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------- CREATE INVENTORY --------------------

    @Test
    void createInventory_success() {
        CreateInventoryRequestDto request = new CreateInventoryRequestDto(1L, 100);

        when(inventoryRepository.existsByProductId(1L)).thenReturn(false);

        Inventory saved = Inventory.builder()
                .productId(1L)
                .totalStock(100)
                .reservedStock(0)
                .soldStock(0)
                .damagedStock(0)
                .build();

        when(inventoryRepository.save(any())).thenReturn(saved);

        InventoryResponseDto response = inventoryService.createInventory(request);

        assertNotNull(response);
        verify(productServiceClient).validateProduct(1L);
        verify(inventoryRepository).save(any());
        verify(inventoryTransactionService)
                .recordTransaction(1L, "STOCK_ADDED", 100, null);
    }

    @Test
    void createInventory_alreadyExists() {
        CreateInventoryRequestDto request = new CreateInventoryRequestDto(1L, 100);

        when(inventoryRepository.existsByProductId(1L)).thenReturn(true);

        assertThrows(InventoryAlreadyExistsException.class,
                () -> inventoryService.createInventory(request));

        verify(inventoryRepository, never()).save(any());
    }

    // -------------------- ADD STOCK --------------------

    @Test
    void addStock_success() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .totalStock(100)
                .reservedStock(0)
                .soldStock(0)
                .damagedStock(0)
                .build();

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);

        StockOperationRequestDto request = new StockOperationRequestDto(50, "ref1");

        inventoryService.addStock(1L, request);

        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(captor.capture());

        assertEquals(150, captor.getValue().getTotalStock());

        verify(inventoryTransactionService)
                .recordTransaction(1L, "STOCK_ADDED", 50, "ref1");
    }

    // -------------------- RESERVE STOCK --------------------

    @Test
    void reserveStock_success() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .totalStock(100)
                .reservedStock(0)
                .soldStock(0)
                .damagedStock(0)
                .build();

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);

        StockOperationRequestDto request = new StockOperationRequestDto(20, "ref1");

        inventoryService.reserveStock(1L, request);

        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(captor.capture());

        assertEquals(20, captor.getValue().getReservedStock());

        verify(inventoryTransactionService)
                .recordTransaction(1L, "STOCK_RESERVED", 20, "ref1");
    }

    @Test
    void reserveStock_insufficientStock() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .totalStock(10)
                .reservedStock(0)
                .soldStock(0)
                .damagedStock(0)
                .build();

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        StockOperationRequestDto request = new StockOperationRequestDto(20, "ref1");

        assertThrows(InsufficientStockException.class,
                () -> inventoryService.reserveStock(1L, request));

        verify(inventoryRepository, never()).save(any());
    }

    // -------------------- CONFIRM SALE --------------------

    @Test
    void confirmSale_success() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .totalStock(100)
                .reservedStock(30)
                .soldStock(0)
                .damagedStock(0)
                .build();

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);

        StockOperationRequestDto request = new StockOperationRequestDto(20, "ref1");

        inventoryService.confirmSale(1L, request);

        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(captor.capture());

        Inventory saved = captor.getValue();
        assertEquals(10, saved.getReservedStock());
        assertEquals(20, saved.getSoldStock());

        verify(inventoryTransactionService)
                .recordTransaction(1L, "SALE_CONFIRMED", 20, "ref1");
    }

    // -------------------- UNDO SALE --------------------

    @Test
    void undoConfirmedSale_success() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .totalStock(100)
                .reservedStock(10)
                .soldStock(20)
                .damagedStock(0)
                .build();

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);

        StockOperationRequestDto request = new StockOperationRequestDto(10, "ref1");

        inventoryService.undoConfirmedSale(1L, request);

        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(captor.capture());

        Inventory saved = captor.getValue();
        assertEquals(10, saved.getSoldStock());
        assertEquals(20, saved.getReservedStock());

        verify(inventoryTransactionService)
                .recordTransaction(1L, "UNDO_SALE", 10, "ref1");
    }

    // -------------------- GET INVENTORY --------------------

    @Test
    void getInventory_success() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .totalStock(100)
                .reservedStock(10)
                .soldStock(10)
                .damagedStock(0)
                .build();

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        InventoryResponseDto response = inventoryService.getInventory(1L);

        assertNotNull(response);
    }

    @Test
    void getInventory_notFound() {
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class,
                () -> inventoryService.getInventory(1L));
    }

    // -------------------- IS IN STOCK --------------------

    @Test
    void isInStock_true() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .totalStock(100)
                .reservedStock(10)
                .soldStock(10)
                .damagedStock(0)
                .build();

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        assertTrue(inventoryService.isInStock(1L, 50));
    }

    @Test
    void isInStock_false() {
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .totalStock(50)
                .reservedStock(20)
                .soldStock(20)
                .damagedStock(0)
                .build();

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        assertFalse(inventoryService.isInStock(1L, 20));
    }
}