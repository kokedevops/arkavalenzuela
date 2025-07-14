import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

package com.arka.arkavalenzuela.model;


class CustomerTest {

    @Test
    void testDefaultConstructor() {
        Customer customer = new Customer();
        assertNull(customer.getClienteId());
        assertNull(customer.getNombre());
        assertNull(customer.getEmail());
        assertNull(customer.getTelefono());
        assertNull(customer.getPais());
        assertNull(customer.getCiudad());
    }

    @Test
    void testSetAndGetClienteId() {
        Customer customer = new Customer();
        customer.setClienteId(123L);
        assertEquals(123L, customer.getClienteId());
    }

    @Test
    void testSetAndGetNombre() {
        Customer customer = new Customer();
        customer.setNombre("Juan Perez");
        assertEquals("Juan Perez", customer.getNombre());
    }

    @Test
    void testSetAndGetEmail() {
        Customer customer = new Customer();
        customer.setEmail("juan@example.com");
        assertEquals("juan@example.com", customer.getEmail());
    }

    @Test
    void testSetAndGetTelefono() {
        Customer customer = new Customer();
        customer.setTelefono("123456789");
        assertEquals("123456789", customer.getTelefono());
    }

    @Test
    void testSetAndGetPais() {
        Customer customer = new Customer();
        customer.setPais("Chile");
        assertEquals("Chile", customer.getPais());
    }

    @Test
    void testSetAndGetCiudad() {
        Customer customer = new Customer();
        customer.setCiudad("Santiago");
        assertEquals("Santiago", customer.getCiudad());
    }
}