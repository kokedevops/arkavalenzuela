import com.arka.arkavalenzuela.model.Product;
import com.arka.arkavalenzuela.model.Categoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

package com.arka.arkavalenzuela.repository;




@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Categoria categoriaRopa;
    private Categoria categoriaZapatos;

    @BeforeEach
    void setUp() {
        categoriaRopa = new Categoria();
        categoriaRopa.setNombre("Ropa");

        categoriaZapatos = new Categoria();
        categoriaZapatos.setNombre("Zapatos");

        Product p1 = new Product();
        p1.setNombre("Camisa Azul");
        p1.setCategoria(categoriaRopa);

        Product p2 = new Product();
        p2.setNombre("Pantalón Negro");
        p2.setCategoria(categoriaRopa);

        Product p3 = new Product();
        p3.setNombre("Zapatos Deportivos");
        p3.setCategoria(categoriaZapatos);

        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
    }

    @Test
    @DisplayName("findByCategoriaNombre returns products by category name")
    void testFindByCategoriaNombre() {
        List<Product> ropaProducts = productRepository.findByCategoriaNombre("Ropa");
        assertEquals(2, ropaProducts.size());
        assertTrue(ropaProducts.stream().allMatch(p -> p.getCategoria().getNombre().equals("Ropa")));

        List<Product> zapatosProducts = productRepository.findByCategoriaNombre("Zapatos");
        assertEquals(1, zapatosProducts.size());
        assertEquals("Zapatos Deportivos", zapatosProducts.get(0).getNombre());
    }

    @Test
    @DisplayName("findByNombreContainingIgnoreCase returns products by partial name, case-insensitive")
    void testFindByNombreContainingIgnoreCase() {
        List<Product> result = productRepository.findByNombreContainingIgnoreCase("camisa");
        assertEquals(1, result.size());
        assertEquals("Camisa Azul", result.get(0).getNombre());

        result = productRepository.findByNombreContainingIgnoreCase("PANTALÓN");
        assertEquals(1, result.size());
        assertEquals("Pantalón Negro", result.get(0).getNombre());

        result = productRepository.findByNombreContainingIgnoreCase("zapatos");
        assertEquals(1, result.size());
        assertEquals("Zapatos Deportivos", result.get(0).getNombre());
    }
}