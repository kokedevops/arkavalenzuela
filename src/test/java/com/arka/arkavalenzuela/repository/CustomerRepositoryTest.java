import com.arka.arkavalenzuela.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

package com.arka.arkavalenzuela.repository;




@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        customerRepository.save(new Customer(null, "Alice", /* other fields as needed */));
        customerRepository.save(new Customer(null, "Alicia", /* other fields as needed */));
        customerRepository.save(new Customer(null, "Bob", /* other fields as needed */));
        customerRepository.save(new Customer(null, "Charlie", /* other fields as needed */));
    }

    @Test
    @DisplayName("findByNombreStartingWith returns customers whose nombre starts with given letter")
    void testFindByNombreStartingWith() {
        List<Customer> result = customerRepository.findByNombreStartingWith("A");
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getNombre().startsWith("A")));

        result = customerRepository.findByNombreStartingWith("B");
        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).getNombre());

        result = customerRepository.findByNombreStartingWith("Z");
        assertTrue(result.isEmpty());
    }
}