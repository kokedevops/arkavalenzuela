import com.arka.arkavalenzuela.model.Order;
import com.arka.arkavalenzuela.model.Product;
import com.arka.arkavalenzuela.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

package com.arka.arkavalenzuela.repository;




@ExtendWith(SpringExtension.class)
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product product1;
    private Product product2;
    private Customer customer1;
    private Customer customer2;
    private Order order1;
    private Order order2;
    private Order order3;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setNombre("Product1");
        entityManager.persist(product1);

        product2 = new Product();
        product2.setNombre("Product2");
        entityManager.persist(product2);

        customer1 = new Customer();
        customer1.setNombre("Customer1");
        entityManager.persist(customer1);

        customer2 = new Customer();
        customer2.setNombre("Customer2");
        entityManager.persist(customer2);

        order1 = new Order();
        order1.setProductos(Arrays.asList(product1, product2));
        order1.setCliente(customer1);
        order1.setFecha(LocalDateTime.now().minusDays(2));
        entityManager.persist(order1);

        order2 = new Order();
        order2.setProductos(Collections.singletonList(product1));
        order2.setCliente(customer2);
        order2.setFecha(LocalDateTime.now().minusDays(1));
        entityManager.persist(order2);

        order3 = new Order();
        order3.setProductos(Collections.singletonList(product2));
        order3.setCliente(customer1);
        order3.setFecha(LocalDateTime.now());
        entityManager.persist(order3);

        entityManager.flush();
    }

    @Test
    void testFindByProductosContaining() {
        List<Order> ordersWithProduct1 = orderRepository.findByProductosContaining(product1);
        assertTrue(ordersWithProduct1.contains(order1));
        assertTrue(ordersWithProduct1.contains(order2));
        assertFalse(ordersWithProduct1.contains(order3));

        List<Order> ordersWithProduct2 = orderRepository.findByProductosContaining(product2);
        assertTrue(ordersWithProduct2.contains(order1));
        assertFalse(ordersWithProduct2.contains(order2));
        assertTrue(ordersWithProduct2.contains(order3));
    }

    @Test
    void testFindByFechaBetween() {
        LocalDateTime start = LocalDateTime.now().minusDays(2).minusHours(1);
        LocalDateTime end = LocalDateTime.now().minusHours(12);

        List<Order> orders = orderRepository.findByFechaBetween(start, end);
        assertTrue(orders.contains(order1));
        assertTrue(orders.contains(order2));
        assertFalse(orders.contains(order3));
    }

    @Test
    void testFindByCliente() {
        List<Order> ordersCustomer1 = orderRepository.findByCliente(customer1);
        assertTrue(ordersCustomer1.contains(order1));
        assertTrue(ordersCustomer1.contains(order3));
        assertFalse(ordersCustomer1.contains(order2));

        List<Order> ordersCustomer2 = orderRepository.findByCliente(customer2);
        assertTrue(ordersCustomer2.contains(order2));
        assertFalse(ordersCustomer2.contains(order1));
        assertFalse(ordersCustomer2.contains(order3));
    }
}