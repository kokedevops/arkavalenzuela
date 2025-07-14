import com.arka.arkavalenzuela.model.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

package com.arka.arkavalenzuela.repository;




@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    private Cart cart1;
    private Cart cart2;
    private Cart cart3;

    @BeforeEach
    void setUp() {
        cart1 = new Cart();
        cart1.setEstado("ACTIVE");
        cartRepository.save(cart1);

        cart2 = new Cart();
        cart2.setEstado("INACTIVE");
        cartRepository.save(cart2);

        cart3 = new Cart();
        cart3.setEstado("ACTIVE");
        cartRepository.save(cart3);
    }

    @Test
    @DisplayName("findByEstado should return carts with matching estado")
    void testFindByEstado() {
        List<Cart> activeCarts = cartRepository.findByEstado("ACTIVE");
        assertEquals(2, activeCarts.size());
        assertTrue(activeCarts.stream().allMatch(c -> "ACTIVE".equals(c.getEstado())));

        List<Cart> inactiveCarts = cartRepository.findByEstado("INACTIVE");
        assertEquals(1, inactiveCarts.size());
        assertEquals("INACTIVE", inactiveCarts.get(0).getEstado());
    }

    @Test
    @DisplayName("findByEstado should return empty list if no match")
    void testFindByEstadoNoMatch() {
        List<Cart> carts = cartRepository.findByEstado("DELETED");
        assertTrue(carts.isEmpty());
    }
}