package com.arka.arkavalenzuela.repository;

import com.arka.arkavalenzuela.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByNombreStartingWith(String letra);
}
