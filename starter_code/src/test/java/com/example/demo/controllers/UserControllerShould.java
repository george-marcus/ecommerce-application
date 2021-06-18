package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerShould {

    private UserController sut;
    private CreateUserRequest createUserRequest;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {

        sut = new UserController();

        TestUtils.injectObjects(sut, "userRepository", userRepository);
        TestUtils.injectObjects(sut, "cartRepository", cartRepository);
        TestUtils.injectObjects(sut, "bCryptPasswordEncoder", bCryptPasswordEncoder);

        // arrange
        when(bCryptPasswordEncoder.encode("rootPassword")).thenReturn("encryptedRoot");

        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("root");
        createUserRequest.setPassword("rootPassword");
        createUserRequest.setConfirmPassword("rootPassword");

    }

    @Test
    public void CreateUserWithCredentials() {

        // act
        ResponseEntity<User> response = sut.createUser(createUserRequest);
        User user = response.getBody();

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("root", user.getUsername());
        assertEquals("encryptedRoot", user.getPassword());
    }

    @Test
    public void findUserById() {

        // arrange
        User user = new User();
        user.setUsername("root");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // act
        ResponseEntity<User> response = sut.findById(1L);
        User responseBody = response.getBody();

        // assert
        assertNotNull(response);
        assertNotNull(responseBody);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, responseBody.getId());
        assertEquals("root", responseBody.getUsername());
    }

    @Test
    public void findUserByName() {

        // arrange
        User user = new User();
        user.setUsername("root");
        user.setId(1L);
        when(userRepository.findByUsername("root")).thenReturn(user);

        // act
        ResponseEntity<User> response = sut.findByUserName("root");
        User responseBody = response.getBody();

        // assert
        assertNotNull(response);
        assertNotNull(responseBody);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, responseBody.getId());
        assertEquals("root", responseBody.getUsername());
    }

    @Test
    public void return404WhenFindUserByNameNotFound() {
        // arrange
        User user = new User();
        user.setUsername("root");
        user.setId(1L);

        when(userRepository.findByUsername("root")).thenReturn(user);

        // act
        ResponseEntity<User> response = sut.findByUserName("non-existent");

        // assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void return400WhenInvalidPasswordProvided() {

        // arrange
        createUserRequest.setPassword("false");
        createUserRequest.setConfirmPassword("false");

        // act
        ResponseEntity<User> response = sut.createUser(createUserRequest);

        // assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void return400WhenIncorrectPasswordConfirmationProvided() {

        // arrange
        createUserRequest.setPassword("rootPassword");
        createUserRequest.setConfirmPassword("differentRootPassword");

        // act
        ResponseEntity<User> response = sut.createUser(createUserRequest);

        // assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

}
