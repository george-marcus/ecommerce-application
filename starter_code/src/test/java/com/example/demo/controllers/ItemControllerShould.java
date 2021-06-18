package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerShould {

    private ItemController sut;
    private Item roundItem;
    private Item squareItem;

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {

        sut = new ItemController();
        TestUtils.injectObjects(sut, "itemRepository", itemRepository);

        roundItem = Item.CreateItem(1L, "Round", new BigDecimal(10), "Round Item");
        squareItem = Item.CreateItem(2L, "Square", new BigDecimal(20), "SquareItem");
    }

    @Test
    public void getItems() {

        // arrange
        when(itemRepository.findAll()).thenReturn(Arrays.asList(roundItem, squareItem));

        // act
        ResponseEntity<List<Item>> response = sut.getItems();
        List<Item> responseBody = response.getBody();

        // assert
        assertNotNull(response);
        assertNotNull(responseBody);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, responseBody.size());
    }

    @Test
    public void getItemById() {
        // arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(roundItem));

        // act
        ResponseEntity<Item> response = sut.getItemById(1L);
        Item responseBody = response.getBody();

        // assert
        assertNotNull(response);
        assertNotNull(responseBody);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Long.valueOf(1L), responseBody.getId());
    }

    @Test
    public void getItemsByName() {

        // arrange
        roundItem.setName("test");
        squareItem.setName("test");
        when(itemRepository.findByName("test")).thenReturn(Arrays.asList(roundItem, squareItem));

        // act
        ResponseEntity<List<Item>> response = sut.getItemsByName("test");
        List<Item> responseBody = response.getBody();

        // assert
        assertNotNull(response);
        assertNotNull(responseBody);

        assertEquals(2, responseBody.size());
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(responseBody.containsAll(Arrays.asList(roundItem, squareItem)));
    }

    @Test
    public void return404WhenNonExistentItem() {

        // arrange
        when(itemRepository.findByName("Square")).thenReturn(Collections.singletonList(squareItem));

        // act
        ResponseEntity<List<Item>> response = sut.getItemsByName("non-existent");
        List<Item> responseBody = response.getBody();


        assertNotNull(response);
        assertNull(responseBody);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void return404WhenNonExistentItemIdProvided() {

        // arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(squareItem));

        // act
        ResponseEntity<Item> response = sut.getItemById(3L);
        Item responseBody = response.getBody();


        assertNotNull(response);
        assertNull(responseBody);
        assertEquals(404, response.getStatusCodeValue());
    }
}
