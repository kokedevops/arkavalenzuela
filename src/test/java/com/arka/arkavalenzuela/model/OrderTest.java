import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

package com.arka.arkavalenzuela.model;



class OrderTest {

    @Test
    void testPedidoIdGetterAndSetter() {
        Order order = new Order();
        order.setPedidoId(123L);
        assertEquals(123L, order.getPedidoId());
    }

    @Test
    void testClienteGetterAndSetter() {
        Order order = new Order();
        Customer customer = new Customer();
        order.setCliente(customer);
        assertSame(customer, order.getCliente());
    }

    @Test
    void testFechaGetterAndSetter() {
        Order order = new Order();
        LocalDateTime now = LocalDateTime.now();
        order.setFecha(now);
        assertEquals(now, order.getFecha());
    }

    @Test
    void testTotalGetterAndSetter() {
        Order order = new Order();
        BigDecimal total = new BigDecimal("99.99");
        order.setTotal(total);
        assertEquals(total, order.getTotal());
    }

    @Test
    void testProductosGetterAndSetter() {
        Order order = new Order();
        Product product1 = new Product();
        Product product2 = new Product();
        Set<Product> products = new HashSet<>();
        products.add(product1);
        products.add(product2);
        order.setProductos(products);
        assertEquals(products, order.getProductos());
    }

    @Test
    void testDefaultValues() {
        Order order = new Order();
        assertNull(order.getPedidoId());
        assertNull(order.getCliente());
        assertNull(order.getFecha());
        assertNull(order.getTotal());
        assertNull(order.getProductos());
    }
}