package ru.mirea.docsa2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mirea.docsa2.client.CustomerClient;
import ru.mirea.docsa2.client.ProductClient;
import ru.mirea.docsa2.dto.CustomerDto;
import ru.mirea.docsa2.dto.ProductDto;
import ru.mirea.docsa2.model.Order;
import ru.mirea.docsa2.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final CustomerClient customerClient;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public List<Order> getOrdersByCustomerId(@PathVariable Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody Order order) {
        try {
            log.info("Creating order for customer {} and product {}", order.getCustomerId(), order.getProductId());

            CustomerDto customer = customerClient.getCustomerById(order.getCustomerId());
            if (customer == null) {
                return ResponseEntity.badRequest().body("Customer not found");
            }

            ProductDto product = productClient.getProductById(order.getProductId());
            if (product == null) {
                return ResponseEntity.badRequest().body("Product not found");
            }

            if (product.getQuantity() < order.getQuantity()) {
                return ResponseEntity.badRequest().body("Insufficient product quantity");
            }

            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(order.getQuantity()));
            order.setId(null);
            order.setTotalPrice(totalPrice);
            order.setStatus(Order.OrderStatus.CONFIRMED);

            Order saved = orderRepository.save(order);
            log.info("Order created successfully: {}", saved.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return ResponseEntity.ok(orderRepository.save(order));
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

