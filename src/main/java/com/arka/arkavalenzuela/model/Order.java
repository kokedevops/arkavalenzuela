
package com.arka.arkavalenzuela.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "pedidos")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pedidoId;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Customer cliente;

    private LocalDateTime fecha;

    private BigDecimal total;

    @ManyToMany
    @JoinTable(
        name = "pedido_producto",
        joinColumns = @JoinColumn(name = "pedido_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private Set<Product> productos;

    // Getters y setters
}
