package ru.mirea.docsa2.controller;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mirea.docsa2.client.CustomerClient;
import ru.mirea.docsa2.client.ProductClient;
import ru.mirea.docsa2.dto.CustomerDto;
import ru.mirea.docsa2.dto.CreateOrderRequest;
import ru.mirea.docsa2.dto.OrderResponse;
import ru.mirea.docsa2.dto.ProductDto;
import ru.mirea.docsa2.model.Order;
import ru.mirea.docsa2.repository.OrderRepository;
import ru.mirea.docsa2.util.AuthenticationUtil;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final CustomerClient customerClient;

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponse> getOrdersByCustomerId(@PathVariable Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/self")
    public ResponseEntity<?> getSelfOrders(Authentication authentication) {
        try {
            Long userId = AuthenticationUtil.extractUserId(authentication);
            if (userId == null) {
                return ResponseEntity.badRequest().body("User ID not found in token");
            }

            CustomerDto customer = customerClient.getCustomerByUserId(userId);
            if (customer == null) {
                return ResponseEntity.badRequest().body("Customer not found for current user");
            }

            List<OrderResponse> orders = orderRepository.findByCustomerId(customer.id()).stream()
                    .map(OrderResponse::from)
                    .toList();

            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching user orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching orders: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request, Authentication authentication) {
        try {
            String username = AuthenticationUtil.extractUsername(authentication);
            Long userId = AuthenticationUtil.extractUserId(authentication);
            
            if (userId == null) {
                return ResponseEntity.badRequest().body("User ID not found in token");
            }
            
            log.info("User '{}' (ID: {}) is creating order for product {}", 
                username, userId, request.productId());

            CustomerDto customer = customerClient.getCustomerByUserId(userId);
            if (customer == null) {
                return ResponseEntity.badRequest().body("Customer not found for current user");
            }

            ProductDto product = productClient.getProductById(request.productId());
            if (product == null) {
                return ResponseEntity.badRequest().body("Product not found");
            }

            if (product.quantity() < request.quantity()) {
                return ResponseEntity.badRequest().body("Insufficient product quantity");
            }

            BigDecimal totalPrice = product.price().multiply(BigDecimal.valueOf(request.quantity()));
            
            Order order = new Order();
            order.setCustomerId(customer.id());
            order.setProductId(request.productId());
            order.setQuantity(request.quantity());
            order.setTotalPrice(totalPrice);
            order.setStatus(Order.OrderStatus.CONFIRMED);

            Order saved = orderRepository.save(order);
            log.info("Order created successfully: {}", saved.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(saved));
        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    Order updated = orderRepository.save(order);
                    return ResponseEntity.ok(OrderResponse.from(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

