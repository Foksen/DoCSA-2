package ru.mirea.docsa2.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;
import ru.mirea.docsa2.dto.CreateCustomerRequest;
import ru.mirea.docsa2.dto.UpdateCustomerRequest;
import ru.mirea.docsa2.dto.CustomerResponse;
import ru.mirea.docsa2.model.Customer;
import ru.mirea.docsa2.repository.CustomerRepository;
import ru.mirea.docsa2.util.AuthenticationUtil;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(CustomerResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomerResponse> getCustomerByUserId(@PathVariable Long userId) {
        return customerRepository.findByUserId(userId)
                .map(CustomerResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/self")
    public ResponseEntity<CustomerResponse> getSelfProfile(Authentication authentication) {
        Long userId = AuthenticationUtil.extractUserId(authentication);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        return customerRepository.findByUserId(userId)
                .map(CustomerResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setUserId(request.userId());
        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
        
        Customer saved = customerRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest request) {
        return customerRepository.findById(id)
                .map(customer -> {
                    if (request.userId() != null) customer.setUserId(request.userId());
                    if (request.name() != null) customer.setName(request.name());
                    if (request.phone() != null) customer.setPhone(request.phone());
                    if (request.address() != null) customer.setAddress(request.address());
                    
                    Customer updated = customerRepository.save(customer);
                    return ResponseEntity.ok(CustomerResponse.from(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (!customerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        customerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

