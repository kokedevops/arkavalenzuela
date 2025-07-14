import com.arka.arkavalenzuela.model.Order;
import com.arka.arkavalenzuela.model.Product;
import com.arka.arkavalenzuela.model.Customer;
import com.arka.arkavalenzuela.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

package com.arka.arkavalenzuela.controller;




public class OrderControllerTest {

    private OrderService service;
    private OrderController controller;

    @Before
    public void setUp() {
        service = mock(OrderService.class);
        controller = new OrderController(service);
    }

    @Test
    public void testGetAllReturnsOrders() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(service.findAll()).thenReturn(orders);

        ResponseEntity<List<Order>> response = controller.getAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, response.getBody());
        verify(service).findAll();
    }

    @Test
    public void testGetByProductReturnsOrders() {
        Long productId = 5L;
        List<Order> orders = Collections.singletonList(new Order());
        when(service.findByProduct(any(Product.class))).thenReturn(orders);

        ResponseEntity<List<Order>> response = controller.getByProduct(productId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, response.getBody());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(service).findByProduct(captor.capture());
        assertEquals(productId, captor.getValue().getProductoId());
    }

    @Test
    public void testGetByDateRangeReturnsOrders() {
        String start = "2023-01-01T00:00:00";
        String end = "2023-01-31T23:59:59";
        LocalDateTime s = LocalDateTime.parse(start);
        LocalDateTime e = LocalDateTime.parse(end);
        List<Order> orders = Arrays.asList(new Order());
        when(service.findByDateRange(s, e)).thenReturn(orders);

        ResponseEntity<List<Order>> response = controller.getByDateRange(start, end);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, response.getBody());
        verify(service).findByDateRange(s, e);
    }

    @Test
    public void testGetByCustomerReturnsOrders() {
        Long customerId = 10L;
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(service.findByCustomer(any(Customer.class))).thenReturn(orders);

        ResponseEntity<List<Order>> response = controller.getByCustomer(customerId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, response.getBody());

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(service).findByCustomer(captor.capture());
        assertEquals(customerId, captor.getValue().getClienteId());
    }
}