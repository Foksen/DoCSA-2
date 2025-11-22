package ru.mirea.docsa2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mirea.docsa2.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

