import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;

package com.arka.arkavalenzuela.model;


public class ProductTest {

    @Test
    public void testDefaultConstructor() {
        Product product = new Product();
        assertNull(product.getProductoId());
        assertNull(product.getNombre());
        assertNull(product.getDescripcion());
        assertNull(product.getCategoria());
        assertNull(product.getMarca());
        assertNull(product.getPrecioUnitario());
        assertNull(product.getStock());
    }

    @Test
    public void testSetAndGetProductoId() {
        Product product = new Product();
        product.setProductoId(10L);
        assertEquals(Long.valueOf(10), product.getProductoId());
    }

    @Test
    public void testSetAndGetNombre() {
        Product product = new Product();
        product.setNombre("Zapato");
        assertEquals("Zapato", product.getNombre());
    }

    @Test
    public void testSetAndGetDescripcion() {
        Product product = new Product();
        product.setDescripcion("Zapato de cuero");
        assertEquals("Zapato de cuero", product.getDescripcion());
    }

    @Test
    public void testSetAndGetCategoria() {
        Product product = new Product();
        Category category = new Category();
        product.setCategoria(category);
        assertEquals(category, product.getCategoria());
    }

    @Test
    public void testSetAndGetMarca() {
        Product product = new Product();
        product.setMarca("Nike");
        assertEquals("Nike", product.getMarca());
    }

    @Test
    public void testSetAndGetPrecioUnitario() {
        Product product = new Product();
        BigDecimal precio = new BigDecimal("123.45");
        product.setPrecioUnitario(precio);
        assertEquals(precio, product.getPrecioUnitario());
    }

    @Test
    public void testSetAndGetStock() {
        Product product = new Product();
        product.setStock(50);
        assertEquals(Integer.valueOf(50), product.getStock());
    }
}