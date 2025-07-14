import com.arka.arkavalenzuela.model.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

package com.arka.arkavalenzuela.repository;




@ExtendWith(SpringExtension.class)
@DataJpaTest
class SupplierRepositoryTest {

    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    @DisplayName("Should save and find Supplier by id")
    void testSaveAndFindById() {
        Supplier supplier = new Supplier();
        supplier.setName("Test Supplier");
        supplier = supplierRepository.save(supplier);

        Optional<Supplier> found = supplierRepository.findById(supplier.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Supplier", found.get().getName());
    }

    @Test
    @DisplayName("Should delete Supplier")
    void testDeleteSupplier() {
        Supplier supplier = new Supplier();
        supplier.setName("To Delete");
        supplier = supplierRepository.save(supplier);

        supplierRepository.deleteById(supplier.getId());
        Optional<Supplier> found = supplierRepository.findById(supplier.getId());
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find all Suppliers")
    void testFindAllSuppliers() {
        Supplier supplier1 = new Supplier();
        supplier1.setName("Supplier 1");
        Supplier supplier2 = new Supplier();
        supplier2.setName("Supplier 2");
        supplierRepository.save(supplier1);
        supplierRepository.save(supplier2);

        Iterable<Supplier> suppliers = supplierRepository.findAll();
        assertTrue(suppliers.iterator().hasNext());
    }
}