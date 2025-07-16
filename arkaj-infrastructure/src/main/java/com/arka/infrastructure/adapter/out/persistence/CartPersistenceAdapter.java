package com.arka.infrastructure.adapter.out.persistence;

import com.arka.domain.model.Cart;
import com.arka.domain.port.out.CartRepositoryPort;
import com.arka.infrastructure.adapter.out.persistence.entity.CartEntity;
import com.arka.infrastructure.adapter.out.persistence.mapper.CartMapper;
import com.arka.infrastructure.adapter.out.persistence.repository.CartJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CartPersistenceAdapter implements CartRepositoryPort {

    private final CartJpaRepository repository;
    private final CartMapper mapper;

    public CartPersistenceAdapter(CartJpaRepository repository, CartMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Cart> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);
        CartEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Cart> findByEstado(String estado) {
        return repository.findByEstado(estado).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
