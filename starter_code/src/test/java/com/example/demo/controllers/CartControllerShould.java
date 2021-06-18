package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerShould {

    private  CartController  sut;
    private ModifyCartRequest modifyCartRequest;


    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup() {
        sut = new CartController();

        TestUtils.injectObjects(sut, "userRepository", userRepository);
        TestUtils.injectObjects(sut, "cartRepository", cartRepository);
        TestUtils.injectObjects(sut, "itemRepository", itemRepository);


        // arrange
        User user = User.CreateUser(1L, "root", "root");

        Item roundItem =  Item.CreateItem(1L, "Round", new BigDecimal(10), "Round item");
        Item squareItem =  Item.CreateItem(2L, "Square", new BigDecimal(20), "Square item");

        List<Item> items = new ArrayList<>();
        items.add(roundItem);
        items.add(squareItem);

        Cart cart = Cart.CreateCart(1L, items, user);
        user.setCart(cart);

        when(userRepository.findByUsername("root")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(roundItem));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(squareItem));
        when(cartRepository.save(cart)).thenReturn(cart);


        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("root");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
    }

    @Test
    public void addToCart() {

        // act
        ResponseEntity<Cart> response = sut.addToCart(modifyCartRequest);
        Cart responseBody = response.getBody();

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(responseBody);
        assertEquals(Long.valueOf(1L), responseBody.getId());
        assertEquals(new BigDecimal(40), responseBody.getTotal());
    }


    @Test
    public void removeItemsFromCart() {

        // act
        ResponseEntity<Cart> response = sut.removeFromCart(modifyCartRequest);
        Cart responseBody = response.getBody();

        // assert
        assertNotNull(response);
        assertNotNull(responseBody);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Long.valueOf(1L), responseBody.getId());
        assertEquals(new BigDecimal(20), responseBody.getTotal());
    }

    @Test
    public void return404WhenAddToCartWithUnAuthorizedUser() {
        // act
        modifyCartRequest.setUsername("unauthorized");
        ResponseEntity<Cart> cartResponseEntity = sut.addToCart(modifyCartRequest);

        // assert
        assertNotNull(cartResponseEntity);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void return404WhenAddingItemWithInvalidId() {

        // act
        modifyCartRequest.setItemId(3L);
        ResponseEntity<Cart> cartResponseEntity = sut.addToCart(modifyCartRequest);

        // assert
        assertNotNull(cartResponseEntity);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void return404WhenUnAuthorizedUserModifiesCart() {
        // act
        modifyCartRequest.setUsername("unauthorized");
        ResponseEntity<Cart> cartResponseEntity = sut.removeFromCart(modifyCartRequest);

        // assert
        assertNotNull(cartResponseEntity);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());

    }

    @Test
    public void return404WhenRemovingItemsWithInvalidIds() {

        // act
        modifyCartRequest.setItemId(3L);
        ResponseEntity<Cart> cartResponseEntity = sut.removeFromCart(modifyCartRequest);

        // assert
        assertNotNull(cartResponseEntity);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());

    }
}
