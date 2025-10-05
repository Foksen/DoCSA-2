package ru.mirea.rsocket;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.mirea.rsocket.controller.OrderController;
import ru.mirea.rsocket.model.Order;
import ru.mirea.rsocket.repository.OrderRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE)
class RSocketServerTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderController deliveryController;

    @BeforeEach
    void init() {
        orderRepository.deleteAll().block();
    }

    @Test
    void testFindOrderById() {
        orderRepository.save(createOrder("Andrew", "Baker St. 2", "bread", "PROCESSING"))
                .flatMap(saved -> deliveryController.findByOrderId(saved.getId()))
                .as(StepVerifier::create)
                .assertNext(fetched -> {
                    assertEquals("Andrew", fetched.getCustomer());
                    assertEquals("bread", fetched.getItems());
                    assertEquals("Baker St. 2", fetched.getAddress());
                })
                .verifyComplete();
    }

    @Test
    void testFindAllOrders() {
        orderRepository.saveAll(List.of(
                        createOrder("Igor", "Baker St. 1", "milk", "CREATED"),
                        createOrder("Andrew", "Baker St. 2", "bread", "PROCESSING"),
                        createOrder("Georgy", "Baker St. 3", "eggs", "DELIVERED")
                ))
                .then()
                .thenMany(deliveryController.findAllOrders())
                .collectList()
                .as(StepVerifier::create)
                .assertNext(list -> {
                    assertEquals(3, list.size());
                    assertTrue(list.stream().anyMatch(o -> o.getCustomer().equals("Igor")
                            && o.getAddress().equals("Baker St. 1")
                            && o.getItems().equals("milk")));
                    assertTrue(list.stream().anyMatch(o -> o.getCustomer().equals("Andrew")
                            && o.getAddress().equals("Baker St. 2")
                            && o.getItems().equals("bread")));
                    assertTrue(list.stream().anyMatch(o -> o.getCustomer().equals("Georgy")
                            && o.getAddress().equals("Baker St. 3")
                            && o.getItems().equals("eggs")));
                })
                .verifyComplete();
    }

    @Test
    void testFindByCustomer() {
        Order igor1 = createOrder("igor", "Baker St. 1", "milk", "CREATED");
        Order igor2 = createOrder("igor", "Baker St. 1", "cheese", "PROCESSING");
        Order igor3 = createOrder("igor", "Baker St. 1", "juice", "DELIVERED");

        orderRepository.saveAll(List.of(igor1, igor2, igor3))
                .then()
                .thenMany(deliveryController.findActiveByCustomer("igor"))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(list -> {
                    assertEquals(2, list.size());
                    assertTrue(list.stream().noneMatch(o -> "DELIVERED".equals(o.getStatus())));
                })
                .verifyComplete();
    }

    @Test
    void testSaveOrders() {
        List<Order> orderList = List.of(
                createOrder("andrew", "Baker St. 2", "apples", null),
                createOrder("andrew", "Baker St. 2", "bananas", null)
        );
        Flux<Order> flux = Flux.fromIterable(orderList);

        deliveryController.saveOrders(flux)
                .as(StepVerifier::create)
                .expectNextMatches(msg -> msg.startsWith("Saved order #"))
                .expectNextMatches(msg -> msg.startsWith("Saved order #"))
                .verifyComplete();

        orderRepository.findAllByCustomer("andrew")
                .as(StepVerifier::create)
                .expectNextMatches(order -> "CREATED".equals(order.getStatus())
                        && order.getAddress().equals("Baker St. 2"))
                .expectNextMatches(order -> "CREATED".equals(order.getStatus())
                        && order.getAddress().equals("Baker St. 2"))
                .verifyComplete();
    }

    @Test
    void testSetOrderDelivered() {
        orderRepository.save(createOrder("Georgy", "Baker St. 3", "eggs", "CREATED"))
                .flatMap(saved -> deliveryController.setOrderDelivered(saved.getId())
                        .then(orderRepository.findById(saved.getId())))
                .as(StepVerifier::create)
                .assertNext(fetched -> {
                    assertEquals("Georgy", fetched.getCustomer());
                    assertEquals("DELIVERED", fetched.getStatus());
                    assertEquals("Baker St. 3", fetched.getAddress());
                })
                .verifyComplete();
    }

    private Order createOrder(String name, String address, String items, String status) {
        return new Order(null, name, address, items, status, Instant.now());
    }
}