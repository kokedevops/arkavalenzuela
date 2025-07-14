package com.arka.arkavalenzuela.controller;

import com.arka.arkavalenzuela.dto.ProductDTO;
import com.arka.arkavalenzuela.model.Category;
import com.arka.arkavalenzuela.model.Product;
import com.arka.arkavalenzuela.service.ProductService;
import com.arka.arkavalenzuela.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    // GET /productos
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    // GET /productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.findById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/categoria/{nombre}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String nombre) {
        List<Product> products = productService.findByCategoriaNombre(nombre);
        return ResponseEntity.ok(products);
    }


    // POST /productos
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO dto) {
        // Validar que la categoría existe
        Category category = categoryRepository.findById(dto.getCategoriaId())
            .orElse(null);
        
        if (category == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: Categoría con ID " + dto.getCategoriaId() + " no encontrada. "
                    + "Primero debe crear la categoría usando POST /categorias");
        }

        Product product = new Product();
        product.setNombre(dto.getNombre());
        product.setDescripcion(dto.getDescripcion());
        product.setCategoria(category);
        product.setMarca(dto.getMarca());
        product.setPrecioUnitario(dto.getPrecioUnitario());
        product.setStock(dto.getStock());

        Product savedProduct = productService.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    // PUT /productos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product existingProduct = productService.findById(id);
        if (existingProduct != null) {
            existingProduct.setNombre(product.getNombre());
            existingProduct.setDescripcion(product.getDescripcion());
            // Note: Para actualizar categoría, necesitarías otro DTO o manejar el ID
            existingProduct.setMarca(product.getMarca());
            existingProduct.setPrecioUnitario(product.getPrecioUnitario());
            existingProduct.setStock(product.getStock());
            Product updated = productService.save(existingProduct);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /productos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product existingProduct = productService.findById(id);
        if (existingProduct != null) {
            productService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /productos/buscar?term=palabra
    @GetMapping("/buscar")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String term) {
        List<Product> products = productService.findAll().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(term.toLowerCase()) ||
                             (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(term.toLowerCase())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    // GET /productos/ordenados
    @GetMapping("/ordenados")
    public ResponseEntity<List<Product>> getAllProductsSorted() {
        List<Product> products = productService.findAll().stream()
                .sorted((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    // GET /productos/rango?min=100&max=500
    @GetMapping("/rango")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<Product> products = productService.findAll().stream()
                .filter(p -> p.getPrecioUnitario() != null &&
                             p.getPrecioUnitario().compareTo(min) >= 0 &&
                             p.getPrecioUnitario().compareTo(max) <= 0)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
}
