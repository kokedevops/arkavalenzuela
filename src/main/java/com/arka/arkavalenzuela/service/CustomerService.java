package com.arka.arkavalenzuela.service;

import com.arka.arkavalenzuela.model.Customer;
import com.arka.arkavalenzuela.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    public Customer findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Customer> findByNombreStartingWith(String letra) {
        return repository.findByNombreStartingWith(letra);
    }
}
