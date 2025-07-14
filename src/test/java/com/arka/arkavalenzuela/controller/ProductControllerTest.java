import com.arka.arkavalenzuela.dto.ProductDTO;
import com.arka.arkavalenzuela.model.Category;
import com.arka.arkavalenzuela.model.Product;
import com.arka.arkavalenzuela.service.ProductService;
import com.arka.arkavalenzuela.repository.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

package com.arka.arkavalenzuela.controller;




public class ProductControllerTest {

    private ProductService productService;
    private CategoryRepository categoryRepository;
    private ProductController controller;

    @Before
    public void setUp() {
        productService = mock(ProductService.class);
        categoryRepository = mock(CategoryRepository.class);
        controller = new ProductController(productService, categoryRepository);
    }

    @Test
    public void testGetAllProducts() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        when(productService.findAll()).thenReturn(products);

        ResponseEntity<List<Product>> response = controller.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }

    @Test
    public void testGetProductById_Found() {
        Product product = new Product();
        when(productService.findById(1L)).thenReturn(product);

        ResponseEntity<Product> response = controller.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
    }

    @Test
    public void testGetProductById_NotFound() {
        when(productService.findById(1L)).thenReturn(null);

        ResponseEntity<Product> response = controller.getProductById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetByCategory() {
        List<Product> products = Arrays.asList(new Product());
        when(productService.findByCategoriaNombre("cat")).thenReturn(products);

        ResponseEntity<List<Product>> response = controller.getByCategory("cat");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }

    @Test
    public void testCreateProduct_CategoryExists() {
        ProductDTO dto = new ProductDTO();
        dto.setCategoriaId(2L);
        dto.setNombre("Prod");
        dto.setDescripcion("Desc");
        dto.setMarca("Marca");
        dto.setPrecioUnitario(BigDecimal.TEN);
        dto.setStock(5);

        Category category = new Category();
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        Product savedProduct = new Product();
        when(productService.save(any(Product.class))).thenReturn(savedProduct);

        ResponseEntity<?> response = controller.createProduct(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedProduct, response.getBody());
    }

    @Test
    public void testCreateProduct_CategoryNotFound() {
        ProductDTO dto = new ProductDTO();
        dto.setCategoriaId(3L);

        when(categoryRepository.findById(3L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.createProduct(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Categoría con ID 3 no encontrada"));
    }

    @Test
    public void testUpdateProduct_Found() {
        Product existing = new Product();
        existing.setId(1L);
        Product update = new Product();
        update.setNombre("Nuevo");
        update.setDescripcion("Desc");
        update.setMarca("Marca");
        update.setPrecioUnitario(BigDecimal.ONE);
        update.setStock(10);

        when(productService.findById(1L)).thenReturn(existing);
        when(productService.save(any(Product.class))).thenReturn(existing);

        ResponseEntity<Product> response = controller.updateProduct(1L, update);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existing, response.getBody());
        assertEquals("Nuevo", existing.getNombre());
        assertEquals("Desc", existing.getDescripcion());
        assertEquals("Marca", existing.getMarca());
        assertEquals(BigDecimal.ONE, existing.getPrecioUnitario());
        assertEquals(10, existing.getStock());
    }

    @Test
    public void testUpdateProduct_NotFound() {
        when(productService.findById(1L)).thenReturn(null);

        ResponseEntity<Product> response = controller.updateProduct(1L, new Product());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteProduct_Found() {
        Product existing = new Product();
        when(productService.findById(1L)).thenReturn(existing);

        ResponseEntity<Void> response = controller.deleteProduct(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService).delete(1L);
    }

    @Test
    public void testDeleteProduct_NotFound() {
        when(productService.findById(1L)).thenReturn(null);

        ResponseEntity<Void> response = controller.deleteProduct(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSearchProducts() {
        Product p1 = new Product();
        p1.setNombre("Zapato");
        p1.setDescripcion("Calzado elegante");
        Product p2 = new Product();
        p2.setNombre("Camisa");
        p2.setDescripcion("Ropa formal");
        when(productService.findAll()).thenReturn(Arrays.asList(p1, p2));

        ResponseEntity<List<Product>> response = controller.searchProducts("zap");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(p1, response.getBody().get(0));
    }

    @Test
    public void testGetAllProductsSorted() {
        Product p1 = new Product();
        p1.setNombre("B");
        Product p2 = new Product();
        p2.setNombre("A");
        when(productService.findAll()).thenReturn(Arrays.asList(p1, p2));

        ResponseEntity<List<Product>> response = controller.getAllProductsSorted();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("A", response.getBody().get(0).getNombre());
        assertEquals("B", response.getBody().get(1).getNombre());
    }

    @Test
    public void testGetProductsByPriceRange() {
        Product p1 = new Product();
        p1.setPrecioUnitario(new BigDecimal("100"));
        Product p2 = new Product();
        p2.setPrecioUnitario(new BigDecimal("200"));
        Product p3 = new Product();
        p3.setPrecioUnitario(new BigDecimal("300"));
        when(productService.findAll()).thenReturn(Arrays.asList(p1, p2, p3));

        ResponseEntity<List<Product>> response = controller.getProductsByPriceRange(
                new BigDecimal("150"), new BigDecimal("300"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(p2));
        assertTrue(response.getBody().contains(p3));
    }
}