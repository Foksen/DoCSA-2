package ru.mirea.rsocket.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mirea.rsocket.model.Order;
import ru.mirea.rsocket.repository.OrderRepository;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderRepository orderRepository;

    @MessageMapping("order.get")
    public Mono<Order> findByOrderId(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @MessageMapping("order.list")
    public Flux<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @MessageMapping("order.stream")
    public Flux<Order> findActiveByCustomer(String customer) {
        return orderRepository.findAllByCustomer(customer)
                .filter(order -> !"DELIVERED".equals(order.getStatus()))
                .doOnCancel(() -> System.out.println("Stream cancelled"));
    }

    @MessageMapping("order.channel")
    public Flux<String> saveOrders(Flux<Order> orders) {
        return orders
                .map(order -> {
                    order.setCreatedAt(Instant.now());
                    order.setStatus("CREATED");
                    return order;
                })
                .flatMap(orderRepository::save)
                .map(saved -> "Saved order #" + saved.getId())
                .take(20);
    }

    @MessageMapping("order.delivered")
    public Mono<Void> setOrderDelivered(Long orderId) {
        return orderRepository.findById(orderId)
                .flatMap(order -> {
                    order.setStatus("DELIVERED");
                    return orderRepository.save(order);
                })
                .then();
    }
}

