package com.arka.arkavalenzuela.domain.port.in;

import com.arka.arkavalenzuela.domain.model.Customer;
import java.util.List;

public interface CustomerUseCase {
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);
    List<Customer> searchCustomersByName(String name);
    List<Customer> getAllCustomersSorted();
}
