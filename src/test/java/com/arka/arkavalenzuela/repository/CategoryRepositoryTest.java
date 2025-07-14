import com.arka.arkavalenzuela.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

package com.arka.arkavalenzuela.repository;




@ExtendWith(SpringExtension.class)
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testSaveAndFindById() {
        Category category = new Category();
        category.setName("Test Category");
        Category saved = categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Category", found.get().getName());
    }

    @Test
    void testFindAll() {
        Category category1 = new Category();
        category1.setName("Category 1");
        Category category2 = new Category();
        category2.setName("Category 2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        List<Category> categories = categoryRepository.findAll();
        assertTrue(categories.size() >= 2);
    }

    @Test
    void testDelete() {
        Category category = new Category();
        category.setName("To Delete");
        Category saved = categoryRepository.save(category);

        categoryRepository.delete(saved);
        Optional<Category> found = categoryRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}