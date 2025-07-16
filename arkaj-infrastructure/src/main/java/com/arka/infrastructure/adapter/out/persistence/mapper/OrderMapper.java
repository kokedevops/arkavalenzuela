package com.arka.infrastructure.adapter.out.persistence.mapper;

import com.arka.domain.model.Order;
import com.arka.domain.model.Product;
import com.arka.infrastructure.adapter.out.persistence.entity.OrderEntity;
import com.arka.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;

    public OrderMapper(CustomerMapper customerMapper, ProductMapper productMapper) {
        this.customerMapper = customerMapper;
        this.productMapper = productMapper;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Order order = new Order();
        order.setId(entity.getPedidoId());
        order.setCliente(customerMapper.toDomain(entity.getCliente()));
        order.setFecha(entity.getFecha());
        order.setTotal(entity.getTotal());
        
        if (entity.getProductos() != null) {
            Set<Product> productos = entity.getProductos().stream()
                    .map(productMapper::toDomain)
                    .collect(Collectors.toSet());
            order.setProductos(productos);
        }
        
        return order;
    }

    public OrderEntity toEntity(Order domain) {
        if (domain == null) {
            return null;
        }
        
        OrderEntity entity = new OrderEntity();
        entity.setPedidoId(domain.getId());
        entity.setCliente(customerMapper.toEntity(domain.getCliente()));
        entity.setFecha(domain.getFecha());
        entity.setTotal(domain.getTotal());
        
        if (domain.getProductos() != null) {
            Set<ProductEntity> productos = domain.getProductos().stream()
                    .map(productMapper::toEntity)
                    .collect(Collectors.toSet());
            entity.setProductos(productos);
        }
        
        return entity;
    }
}
