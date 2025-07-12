package com.arka.arkavalenzuela.controller;

import com.arka.arkavalenzuela.model.Customer;
import com.arka.arkavalenzuela.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // GET /usuarios
    @GetMapping
    public ResponseEntity<List<Customer>> getAllUsers() {
        List<Customer> customers = customerService.findAll();
        return ResponseEntity.ok(customers);
    }

    // GET /usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getUserById(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /usuarios
    @PostMapping
    public ResponseEntity<Customer> createUser(@RequestBody Customer customer) {
        Customer savedCustomer = customerService.save(customer);
        return ResponseEntity.ok(savedCustomer);
    }

    // PUT /usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateUser(@PathVariable Long id, @RequestBody Customer customer) {
        Customer existingCustomer = customerService.findById(id);
        if (existingCustomer != null) {
            existingCustomer.setNombre(customer.getNombre());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setTelefono(customer.getTelefono());
            existingCustomer.setPais(customer.getPais());
            existingCustomer.setCiudad(customer.getCiudad());
            Customer updated = customerService.save(existingCustomer);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Customer existingCustomer = customerService.findById(id);
        if (existingCustomer != null) {
            customerService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /usuarios/buscar?nombre=Alfa
    @GetMapping("/buscar")
    public ResponseEntity<List<Customer>> searchByName(@RequestParam String nombre) {
        List<Customer> customers = customerService.findByNombreStartingWith(nombre);
        return ResponseEntity.ok(customers);
    }

    // GET /usuarios/ordenados
    @GetMapping("/ordenados")
    public ResponseEntity<List<Customer>> getAllUsersSorted() {
        List<Customer> customers = customerService.findAll();
        customers.sort((c1, c2) -> c1.getNombre().compareToIgnoreCase(c2.getNombre()));
        return ResponseEntity.ok(customers);
    }
}
