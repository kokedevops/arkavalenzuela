package com.arka.infrastructure.adapter.out.persistence.mapper;

import com.arka.domain.model.Cart;
import com.arka.infrastructure.adapter.out.persistence.entity.CartEntity;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    private final CustomerMapper customerMapper;

    public CartMapper(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    public Cart toDomain(CartEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Cart cart = new Cart();
        cart.setId(entity.getCarritoId());
        cart.setCliente(customerMapper.toDomain(entity.getCliente()));
        cart.setFechaCreacion(entity.getFechaCreacion());
        cart.setEstado(entity.getEstado());
        
        return cart;
    }

    public CartEntity toEntity(Cart domain) {
        if (domain == null) {
            return null;
        }
        
        CartEntity entity = new CartEntity();
        entity.setCarritoId(domain.getId());
        entity.setCliente(customerMapper.toEntity(domain.getCliente()));
        entity.setFechaCreacion(domain.getFechaCreacion());
        entity.setEstado(domain.getEstado());
        
        return entity;
    }
}
