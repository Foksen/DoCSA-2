package ru.mirea.rsocket.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.mirea.rsocket.model.Order;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    Flux<Order> findAllByCustomer(String customer);
}
