package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper;

import com.arka.arkavalenzuela.domain.model.Customer;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Customer customer = new Customer();
        customer.setId(entity.getClienteId());
        customer.setNombre(entity.getNombre());
        customer.setEmail(entity.getEmail());
        customer.setTelefono(entity.getTelefono());
        customer.setPais(entity.getPais());
        customer.setCiudad(entity.getCiudad());
        
        return customer;
    }

    public CustomerEntity toEntity(Customer domain) {
        if (domain == null) {
            return null;
        }
        
        CustomerEntity entity = new CustomerEntity();
        entity.setClienteId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setEmail(domain.getEmail());
        entity.setTelefono(domain.getTelefono());
        entity.setPais(domain.getPais());
        entity.setCiudad(domain.getCiudad());
        
        return entity;
    }
}
