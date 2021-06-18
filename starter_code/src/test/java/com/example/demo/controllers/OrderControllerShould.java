package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class OrderControllerShould {

    private OrderController sut;
    private User user;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup() {
        sut = new OrderController();

        TestUtils.injectObjects(sut, "userRepository", userRepository);
        TestUtils.injectObjects(sut, "orderRepository", orderRepository);

        // arrange
        List<Item> items = new ArrayList<>();
        Item roundItem = Item.CreateItem(1L, "Round", new BigDecimal(10), "Round Item");
        Item squareItem = Item.CreateItem(2L, "Square", new BigDecimal(20), "SquareItem");

        items.add(roundItem);
        items.add(squareItem);

        user =  User.CreateUser(1L, "root", "root");

        Cart cart = Cart.CreateCart(1L, items, user);
        user.setCart(cart);

        UserOrder userOrder = new UserOrder();

        userOrder.setId(1L);
        userOrder.setUser(user);
        userOrder.setItems(items);
        userOrder.setTotal(cart.getTotal());

        when(userRepository.findByUsername("root")).thenReturn(user);
        when(orderRepository.save(userOrder)).thenReturn(userOrder);
        when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(userOrder));

    }

    @Test
    public void submitOrder() {

        // act
        ResponseEntity<UserOrder> response = sut.submit(user.getUsername());
        UserOrder responseBody = response.getBody();

        // assert
        assertNotNull(response);
        assertNotNull(responseBody);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, responseBody.getItems().size());
        assertEquals("root", responseBody.getUser().getUsername());
        assertEquals(new BigDecimal(30), responseBody.getTotal());
    }

    @Test
    public void getOrdersForUser() {

        // act
        ResponseEntity<List<UserOrder>> response = sut.getOrdersForUser(user.getUsername());
        List<UserOrder> responseBody = response.getBody();

        assertNotNull(response);
        assertNotNull(responseBody);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, responseBody.get(0).getItems().size());
        assertEquals("root", responseBody.get(0).getUser().getUsername());
        assertEquals(new BigDecimal(30), responseBody.get(0).getTotal());
    }

    @Test
    public void return404WhenUnAuthorizedUserGetsTheirOrders() {

        // act
        ResponseEntity<List<UserOrder>> response = sut.getOrdersForUser("unauthorized");

        //
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void return404WhenUnAuthorizedUserSubmitsOrder() {

        // act
        ResponseEntity<UserOrder> response = sut.submit("unauthorized");

        // assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
