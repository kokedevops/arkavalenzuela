package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence;

import com.arka.arkavalenzuela.domain.model.Customer;
import com.arka.arkavalenzuela.domain.port.out.CustomerRepositoryPort;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper.CustomerMapper;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository.CustomerJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository repository;
    private final CustomerMapper mapper;

    public CustomerPersistenceAdapter(CustomerJpaRepository repository, CustomerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = mapper.toEntity(customer);
        CustomerEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Customer> findByNombreStartingWith(String letra) {
        return repository.findByNombreStartingWith(letra).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
