import com.arka.arkavalenzuela.model.Customer;
import com.arka.arkavalenzuela.service.CustomerService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.http.ResponseEntity;

package com.arka.arkavalenzuela.controller;




public class CustomerControllerTest {

    private CustomerService customerService;
    private CustomerController customerController;

    @Before
    public void setUp() {
        customerService = mock(CustomerService.class);
        customerController = new CustomerController(customerService);
    }

    @Test
    public void testGetAllUsers() {
        Customer c1 = new Customer();
        Customer c2 = new Customer();
        List<Customer> customers = Arrays.asList(c1, c2);
        when(customerService.findAll()).thenReturn(customers);

        ResponseEntity<List<Customer>> response = customerController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(customers, response.getBody());
    }

    @Test
    public void testGetUserById_Found() {
        Customer c = new Customer();
        when(customerService.findById(1L)).thenReturn(c);

        ResponseEntity<Customer> response = customerController.getUserById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(c, response.getBody());
    }

    @Test
    public void testGetUserById_NotFound() {
        when(customerService.findById(1L)).thenReturn(null);

        ResponseEntity<Customer> response = customerController.getUserById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testCreateUser() {
        Customer input = new Customer();
        Customer saved = new Customer();
        when(customerService.save(input)).thenReturn(saved);

        ResponseEntity<Customer> response = customerController.createUser(input);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(saved, response.getBody());
    }

    @Test
    public void testUpdateUser_Found() {
        Customer existing = new Customer();
        existing.setNombre("Old");
        existing.setEmail("old@mail.com");
        existing.setTelefono("123");
        existing.setPais("OldCountry");
        existing.setCiudad("OldCity");

        Customer update = new Customer();
        update.setNombre("New");
        update.setEmail("new@mail.com");
        update.setTelefono("456");
        update.setPais("NewCountry");
        update.setCiudad("NewCity");

        Customer updated = new Customer();
        when(customerService.findById(1L)).thenReturn(existing);
        when(customerService.save(existing)).thenReturn(updated);

        ResponseEntity<Customer> response = customerController.updateUser(1L, update);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
        assertEquals("New", existing.getNombre());
        assertEquals("new@mail.com", existing.getEmail());
        assertEquals("456", existing.getTelefono());
        assertEquals("NewCountry", existing.getPais());
        assertEquals("NewCity", existing.getCiudad());
    }

    @Test
    public void testUpdateUser_NotFound() {
        when(customerService.findById(1L)).thenReturn(null);
        Customer update = new Customer();

        ResponseEntity<Customer> response = customerController.updateUser(1L, update);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteUser_Found() {
        Customer existing = new Customer();
        when(customerService.findById(1L)).thenReturn(existing);

        ResponseEntity<Void> response = customerController.deleteUser(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(customerService).delete(1L);
    }

    @Test
    public void testDeleteUser_NotFound() {
        when(customerService.findById(1L)).thenReturn(null);

        ResponseEntity<Void> response = customerController.deleteUser(1L);

        assertEquals(404, response.getStatusCodeValue());
        verify(customerService, never()).delete(anyLong());
    }

    @Test
    public void testSearchByName() {
        List<Customer> customers = Arrays.asList(new Customer(), new Customer());
        when(customerService.findByNombreStartingWith("Alfa")).thenReturn(customers);

        ResponseEntity<List<Customer>> response = customerController.searchByName("Alfa");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(customers, response.getBody());
    }

    @Test
    public void testGetAllUsersSorted() {
        Customer c1 = new Customer();
        c1.setNombre("Beta");
        Customer c2 = new Customer();
        c2.setNombre("Alfa");
        List<Customer> customers = Arrays.asList(c1, c2);
        when(customerService.findAll()).thenReturn(customers);

        ResponseEntity<List<Customer>> response = customerController.getAllUsersSorted();

        assertEquals(200, response.getStatusCodeValue());
        List<Customer> sorted = response.getBody();
        assertEquals("Alfa", sorted.get(0).getNombre());
        assertEquals("Beta", sorted.get(1).getNombre());
    }
}