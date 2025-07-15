package com.arka.arkavalenzuela.infrastructure.config;

import com.arka.arkavalenzuela.domain.port.in.*;
import com.arka.arkavalenzuela.domain.port.out.*;
import com.arka.arkavalenzuela.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ProductUseCase productUseCase(ProductRepositoryPort productRepository, 
                                       CategoryRepositoryPort categoryRepository) {
        return new ProductDomainService(productRepository, categoryRepository);
    }

    @Bean
    public CategoryUseCase categoryUseCase(CategoryRepositoryPort categoryRepository) {
        return new CategoryDomainService(categoryRepository);
    }

    @Bean
    public CustomerUseCase customerUseCase(CustomerRepositoryPort customerRepository) {
        return new CustomerDomainService(customerRepository);
    }

    @Bean
    public OrderUseCase orderUseCase(OrderRepositoryPort orderRepository) {
        return new OrderDomainService(orderRepository);
    }

    @Bean
    public CartUseCase cartUseCase(CartRepositoryPort cartRepository) {
        return new CartDomainService(cartRepository);
    }
}
