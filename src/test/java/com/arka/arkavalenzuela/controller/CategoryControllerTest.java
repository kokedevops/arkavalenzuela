import com.arka.arkavalenzuela.dto.CategoryDTO;
import com.arka.arkavalenzuela.model.Category;
import com.arka.arkavalenzuela.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.http.ResponseEntity;

package com.arka.arkavalenzuela.controller;




class CategoryControllerTest {

    private CategoryRepository categoryRepository;
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
    }

    @Test
    void testGetAllCategories() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setNombre("Cat1");
        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setNombre("Cat2");
        List<Category> categories = Arrays.asList(cat1, cat2);

        when(categoryRepository.findAll()).thenReturn(categories);

        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Cat1", response.getBody().get(0).getNombre());
    }

    @Test
    void testGetCategoryById_Found() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setNombre("Cat1");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));

        ResponseEntity<Category> response = categoryController.getCategoryById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Cat1", response.getBody().getNombre());
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Category> response = categoryController.getCategoryById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testCreateCategory() {
        CategoryDTO dto = new CategoryDTO();
        dto.setNombre("NewCat");

        Category saved = new Category();
        saved.setId(10L);
        saved.setNombre("NewCat");

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        ResponseEntity<Category> response = categoryController.createCategory(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("NewCat", response.getBody().getNombre());

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("NewCat", captor.getValue().getNombre());
    }

    @Test
    void testUpdateCategory_Found() {
        Category existing = new Category();
        existing.setId(5L);
        existing.setNombre("OldName");

        CategoryDTO dto = new CategoryDTO();
        dto.setNombre("UpdatedName");

        Category updated = new Category();
        updated.setId(5L);
        updated.setNombre("UpdatedName");

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(updated);

        ResponseEntity<Category> response = categoryController.updateCategory(5L, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("UpdatedName", response.getBody().getNombre());
    }

    @Test
    void testUpdateCategory_NotFound() {
        CategoryDTO dto = new CategoryDTO();
        dto.setNombre("UpdatedName");

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Category> response = categoryController.updateCategory(99L, dto);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteCategory_Found() {
        when(categoryRepository.existsById(3L)).thenReturn(true);

        ResponseEntity<Void> response = categoryController.deleteCategory(3L);

        assertEquals(204, response.getStatusCodeValue());
        verify(categoryRepository).deleteById(3L);
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(categoryRepository.existsById(4L)).thenReturn(false);

        ResponseEntity<Void> response = categoryController.deleteCategory(4L);

        assertEquals(404, response.getStatusCodeValue());
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}