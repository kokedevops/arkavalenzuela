import com.arka.arkavalenzuela.model.Cart;
import com.arka.arkavalenzuela.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

package com.arka.arkavalenzuela.controller;




class CartControllerTest {

    private CartService cartService;
    private CartController cartController;

    @BeforeEach
    void setUp() {
        cartService = mock(CartService.class);
        cartController = new CartController(cartService);
    }

    @Test
    void getAbandonedCarts_returnsListOfAbandonedCarts() {
        Cart cart1 = new Cart();
        Cart cart2 = new Cart();
        List<Cart> abandonedCarts = Arrays.asList(cart1, cart2);

        when(cartService.findByEstado("ABANDONED")).thenReturn(abandonedCarts);

        ResponseEntity<List<Cart>> response = cartController.getAbandonedCarts();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(abandonedCarts, response.getBody());
        verify(cartService, times(1)).findByEstado("ABANDONED");
    }

    @Test
    void getAbandonedCarts_returnsEmptyListWhenNoAbandonedCarts() {
        when(cartService.findByEstado("ABANDONED")).thenReturn(Collections.emptyList());

        ResponseEntity<List<Cart>> response = cartController.getAbandonedCarts();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(cartService, times(1)).findByEstado("ABANDONED");
    }
}s