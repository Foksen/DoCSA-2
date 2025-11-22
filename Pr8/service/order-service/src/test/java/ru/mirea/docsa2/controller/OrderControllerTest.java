package ru.mirea.docsa2.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.mirea.docsa2.client.CustomerClient;
import ru.mirea.docsa2.client.ProductClient;
import ru.mirea.docsa2.dto.CreateOrderRequest;
import ru.mirea.docsa2.dto.CustomerDto;
import ru.mirea.docsa2.dto.OrderResponse;
import ru.mirea.docsa2.dto.ProductDto;
import ru.mirea.docsa2.model.Order;
import ru.mirea.docsa2.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private OrderController orderController;

    @Test
    void getAllOrders_ShouldReturnListOfOrders() {
        Order order = createOrder(1L, 1L, 1L, 2, new BigDecimal("3000.00"));
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<OrderResponse> result = orderController.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("3000.00"), result.get(0).totalPrice());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_WhenExists_ShouldReturnOrder() {
        Order order = createOrder(1L, 1L, 1L, 2, new BigDecimal("3000.00"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        ResponseEntity<OrderResponse> response = orderController.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getSelfOrders_WhenCustomerExists_ShouldReturnOrders() {
        Authentication auth = createMockAuthentication(100L);
        CustomerDto customer = new CustomerDto(1L, 100L, "Ivan", "+7-999-123", "Moscow");
        Order order = createOrder(1L, 1L, 1L, 2, new BigDecimal("3000.00"));
        
        when(customerClient.getCustomerByUserId(100L)).thenReturn(customer);
        when(orderRepository.findByCustomerId(1L)).thenReturn(List.of(order));

        ResponseEntity<?> response = orderController.getSelfOrders(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getSelfOrders_WhenUserIdNull_ShouldReturnBadRequest() {
        Authentication auth = createMockAuthentication(null);

        ResponseEntity<?> response = orderController.getSelfOrders(auth);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createOrder_WhenValid_ShouldReturnCreatedOrder() {
        CreateOrderRequest request = new CreateOrderRequest(1L, 2);
        Authentication auth = createMockAuthentication(100L);
        CustomerDto customer = new CustomerDto(1L, 100L, "Ivan", "+7-999-123", "Moscow");
        ProductDto product = new ProductDto(1L, "Laptop", new BigDecimal("1500.00"), 10);
        Order savedOrder = createOrder(1L, 1L, 1L, 2, new BigDecimal("3000.00"));
        
        when(customerClient.getCustomerByUserId(100L)).thenReturn(customer);
        when(productClient.getProductById(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<?> response = orderController.createOrder(request, auth);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_WhenInsufficientQuantity_ShouldReturnBadRequest() {
        CreateOrderRequest request = new CreateOrderRequest(1L, 20);
        Authentication auth = createMockAuthentication(100L);
        CustomerDto customer = new CustomerDto(1L, 100L, "Ivan", "+7-999-123", "Moscow");
        ProductDto product = new ProductDto(1L, "Laptop", new BigDecimal("1500.00"), 10);
        
        when(customerClient.getCustomerByUserId(100L)).thenReturn(customer);
        when(productClient.getProductById(1L)).thenReturn(product);

        ResponseEntity<?> response = orderController.createOrder(request, auth);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WhenCustomerNotFound_ShouldReturnBadRequest() {
        CreateOrderRequest request = new CreateOrderRequest(1L, 2);
        Authentication auth = createMockAuthentication(100L);
        
        when(customerClient.getCustomerByUserId(100L)).thenReturn(null);

        ResponseEntity<?> response = orderController.createOrder(request, auth);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateOrderStatus_WhenExists_ShouldReturnUpdatedOrder() {
        Order order = createOrder(1L, 1L, 1L, 2, new BigDecimal("3000.00"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        ResponseEntity<OrderResponse> response = orderController.updateOrderStatus(1L, Order.OrderStatus.COMPLETED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void deleteOrder_WhenExists_ShouldReturn204() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderRepository, times(1)).deleteById(1L);
    }

    private Order createOrder(Long id, Long customerId, Long productId, Integer quantity, BigDecimal totalPrice) {
        Order order = new Order();
        order.setId(id);
        order.setCustomerId(customerId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    private Authentication createMockAuthentication(Long userId) {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaims()).thenReturn(userId != null ? Map.of("userId", userId) : Map.of());
        lenient().when(jwt.getClaimAsString("sub")).thenReturn("testuser");
        return auth;
    }
}

