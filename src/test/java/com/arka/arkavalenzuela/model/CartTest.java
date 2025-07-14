import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

package com.arka.arkavalenzuela.model;



class CartTest {

    @Test
    void testCarritoIdGetterAndSetter() {
        Cart cart = new Cart();
        cart.setCarritoId(123L);
        assertEquals(123L, cart.getCarritoId());
    }

    @Test
    void testClienteGetterAndSetter() {
        Cart cart = new Cart();
        Customer mockCustomer = mock(Customer.class);
        cart.setCliente(mockCustomer);
        assertEquals(mockCustomer, cart.getCliente());
    }

    @Test
    void testFechaCreacionGetterAndSetter() {
        Cart cart = new Cart();
        LocalDateTime now = LocalDateTime.now();
        cart.setFechaCreacion(now);
        assertEquals(now, cart.getFechaCreacion());
    }

    @Test
    void testEstadoGetterAndSetter() {
        Cart cart = new Cart();
        cart.setEstado("activo");
        assertEquals("activo", cart.getEstado());
    }

    @Test
    void testDefaultValues() {
        Cart cart = new Cart();
        assertNull(cart.getCarritoId());
        assertNull(cart.getCliente());
        assertNull(cart.getFechaCreacion());
        assertNull(cart.getEstado());
    }

    @Test
    void testSettersAndGettersWithNull() {
        Cart cart = new Cart();
        cart.setCarritoId(null);
        cart.setCliente(null);
        cart.setFechaCreacion(null);
        cart.setEstado(null);

        assertNull(cart.getCarritoId());
        assertNull(cart.getCliente());
        assertNull(cart.getFechaCreacion());
        assertNull(cart.getEstado());
    }

    @Test
    void testMultipleSettersAndGetters() {
        Cart cart = new Cart();
        Customer mockCustomer = mock(Customer.class);
        LocalDateTime dateTime = LocalDateTime.of(2023, 1, 1, 12, 0);
        cart.setCarritoId(1L);
        cart.setCliente(mockCustomer);
        cart.setFechaCreacion(dateTime);
        cart.setEstado("pendiente");

        assertEquals(1L, cart.getCarritoId());
        assertEquals(mockCustomer, cart.getCliente());
        assertEquals(dateTime, cart.getFechaCreacion());
        assertEquals("pendiente", cart.getEstado());
    }
}