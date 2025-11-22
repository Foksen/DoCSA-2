package ru.mirea.docsa2.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.mirea.docsa2.dto.CustomerDto;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/customers/{id}")
    CustomerDto getCustomerById(@PathVariable Long id);

    @GetMapping("/customers/user/{userId}")
    CustomerDto getCustomerByUserId(@PathVariable Long userId);
}

