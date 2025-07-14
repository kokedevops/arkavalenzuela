import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

package com.arka.arkavalenzuela.model;



class CategoryTest {

    @Test
    void testGetAndSetCategoriaId() {
        Category category = new Category();
        category.setCategoriaId(10L);
        assertEquals(10L, category.getCategoriaId());
    }

    @Test
    void testGetAndSetNombre() {
        Category category = new Category();
        category.setNombre("Electronics");
        assertEquals("Electronics", category.getNombre());
    }

    @Test
    void testGetAndSetProductos() {
        Category category = new Category();
        Product product1 = new Product();
        Product product2 = new Product();
        List<Product> productos = Arrays.asList(product1, product2);
        category.setProductos(productos);
        assertEquals(productos, category.getProductos());
    }

    @Test
    void testProductosInitiallyNull() {
        Category category = new Category();
        assertNull(category.getProductos());
    }
}