package ru.mirea.rsocket;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import reactor.core.publisher.Flux;
import ru.mirea.rsocket.model.Order;

import java.util.ArrayList;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderShell {

    RSocketRequester requester;

    // find-by-order-id 123
    @ShellMethod("Find order by ID")
    public void findOrderById(long orderId) {
        requester.route("order.get").data(orderId)
                .retrieveMono(Order.class)
                .doOnNext(System.out::println)
                .block();
    }

    // find-all-orders
    @ShellMethod("Find all orders")
    public void findAllOrders() {
        requester.route("order.list")
                .retrieveFlux(Order.class)
                .doOnNext(System.out::println)
                .blockLast();
    }

    // find-active-by-customer Igor
    @ShellMethod("Find active by customer")
    public void findActiveByCustomer(String customer) {
        requester.route("order.stream").data(customer)
                .retrieveFlux(Order.class)
                .doOnNext(System.out::println)
                .blockLast();
    }

    // add-orders "Igor|Moscow|Apples" "Matvey|NN|Oranges"
    @ShellMethod("Add orders")
    public void addOrders(String... orders) {
        List<Order> orderList = new ArrayList<>();
        for (String s : orders) {
            String[] parts = s.split("\\|", 3);
            if (parts.length != 3) {
                continue;
            }
            orderList.add(new Order(null, parts[0], parts[1], parts[2], null, null));
        }
        if (orderList.isEmpty()) {
            return;
        }
        requester.route("order.channel")
                .data(Flux.fromIterable(orderList))
                .retrieveFlux(String.class)
                .doOnNext(System.out::println)
                .blockLast();
    }

    // set-order-delivered 123
    @ShellMethod("Set order delivered")
    public void setOrderDelivered(long orderId) {
        requester.route("order.delivered").data(orderId)
                .send()
                .doOnTerminate(() -> System.out.println("Order " + orderId + " set delivered"))
                .block();
    }
}
