package com.arka.arkavalenzuela.controller;

import com.arka.arkavalenzuela.model.Product;
import com.arka.arkavalenzuela.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
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

    // POST /productos
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
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
            existingProduct.setCategoria(product.getCategoria());
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
