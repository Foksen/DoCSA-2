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
import ru.mirea.docsa2.dto.CreateCustomerRequest;
import ru.mirea.docsa2.dto.CustomerResponse;
import ru.mirea.docsa2.dto.UpdateCustomerRequest;
import ru.mirea.docsa2.model.Customer;
import ru.mirea.docsa2.repository.CustomerRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void getAllCustomers_ShouldReturnListOfCustomers() {
        Customer customer = new Customer(1L, 100L, "Ivan", "+7-999-123-45-67", "Moscow");
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerResponse> result = customerController.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("Ivan", result.get(0).name());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getCustomerById_WhenExists_ShouldReturnCustomer() {
        Customer customer = new Customer(1L, 100L, "Ivan", "+7-999-123-45-67", "Moscow");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        ResponseEntity<CustomerResponse> response = customerController.getCustomerById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ivan", response.getBody().name());
    }

    @Test
    void getCustomerByUserId_WhenExists_ShouldReturnCustomer() {
        Customer customer = new Customer(1L, 100L, "Ivan", "+7-999-123-45-67", "Moscow");
        when(customerRepository.findByUserId(100L)).thenReturn(Optional.of(customer));

        ResponseEntity<CustomerResponse> response = customerController.getCustomerByUserId(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().userId());
    }

    @Test
    void getSelfProfile_WhenUserIdExists_ShouldReturnProfile() {
        Authentication auth = createMockAuthentication(100L);
        Customer customer = new Customer(1L, 100L, "Ivan", "+7-999-123-45-67", "Moscow");
        when(customerRepository.findByUserId(100L)).thenReturn(Optional.of(customer));

        ResponseEntity<CustomerResponse> response = customerController.getSelfProfile(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ivan", response.getBody().name());
    }

    @Test
    void getSelfProfile_WhenUserIdNull_ShouldReturnBadRequest() {
        Authentication auth = createMockAuthentication(null);

        ResponseEntity<CustomerResponse> response = customerController.getSelfProfile(auth);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() {
        Authentication auth = createMockAuthentication(100L);
        CreateCustomerRequest request = new CreateCustomerRequest("Ivan", "+7-999-123-45-67", "Moscow");
        Customer savedCustomer = new Customer(1L, 100L, "Ivan", "+7-999-123-45-67", "Moscow");
        
        when(customerRepository.findByUserId(100L)).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        ResponseEntity<CustomerResponse> response = customerController.createCustomer(request, auth);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ivan", response.getBody().name());
        assertEquals(100L, response.getBody().userId());
    }

    @Test
    void createCustomer_WhenCustomerAlreadyExists_ShouldReturnConflict() {
        Authentication auth = createMockAuthentication(100L);
        CreateCustomerRequest request = new CreateCustomerRequest("Ivan", "+7-999-123-45-67", "Moscow");
        Customer existingCustomer = new Customer(1L, 100L, "Existing", "+7-999-000-00-00", "Moscow");
        
        when(customerRepository.findByUserId(100L)).thenReturn(Optional.of(existingCustomer));

        ResponseEntity<CustomerResponse> response = customerController.createCustomer(request, auth);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_WhenUserIdNull_ShouldReturnBadRequest() {
        Authentication auth = createMockAuthentication(null);
        CreateCustomerRequest request = new CreateCustomerRequest("Ivan", "+7-999-123-45-67", "Moscow");

        ResponseEntity<CustomerResponse> response = customerController.createCustomer(request, auth);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_WhenExists_ShouldReturnUpdatedCustomer() {
        Authentication auth = createMockAuthentication(100L);
        UpdateCustomerRequest request = new UpdateCustomerRequest("Petr", "+7-999-111-22-33", null);
        Customer existingCustomer = new Customer(1L, 100L, "Ivan", "+7-999-123-45-67", "Moscow");
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        ResponseEntity<CustomerResponse> response = customerController.updateCustomer(1L, request, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_WhenNotOwnProfile_ShouldReturnForbidden() {
        Authentication auth = createMockAuthentication(100L);
        UpdateCustomerRequest request = new UpdateCustomerRequest("Petr", "+7-999-111-22-33", null);
        Customer existingCustomer = new Customer(1L, 999L, "Ivan", "+7-999-123-45-67", "Moscow");
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));

        ResponseEntity<CustomerResponse> response = customerController.updateCustomer(1L, request, auth);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_WhenUserIdNull_ShouldReturnBadRequest() {
        Authentication auth = createMockAuthentication(null);
        UpdateCustomerRequest request = new UpdateCustomerRequest("Petr", "+7-999-111-22-33", null);

        ResponseEntity<CustomerResponse> response = customerController.updateCustomer(1L, request, auth);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_WhenExists_ShouldReturn204() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = customerController.deleteCustomer(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerRepository, times(1)).deleteById(1L);
    }

    private Authentication createMockAuthentication(Long userId) {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaims()).thenReturn(Map.of("userId", userId != null ? userId : ""));
        return auth;
    }
}

