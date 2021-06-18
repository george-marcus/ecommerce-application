package com.example.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;

@RequestMapping("/api/user")
public class UserController extends  BaseController{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		Optional<User> user = userRepository.findById(id);

		if(!user.isPresent()){
			logger.warn("User with id {} couldn't be found", id);
			return ResponseEntity.notFound().build();
		}

		logger.info("Found a user with id {}", id);
		return ResponseEntity.ok(user.get());
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {

		User user = userRepository.findByUsername(username);

		if(user == null){
			logger.warn("User with username {} couldn't be found", username);
			return ResponseEntity.notFound().build();
		}

		logger.info("Found a user with name {}", user.getUsername());
		return  ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {

		String requestPassword = createUserRequest.getPassword();

		if(requestPassword.length() < 7 ||
				!requestPassword.equals(createUserRequest.getConfirmPassword()))		{
			logger.warn("user password should be at least 8 chars and match the confirm password value");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		Cart cart = new Cart();
		cartRepository.save(cart);

		String encryptedPassword = bCryptPasswordEncoder.encode(requestPassword);

		User user = new User();

		user.setUsername(createUserRequest.getUsername());
		user.setCart(cart);
		user.setPassword(encryptedPassword);

		userRepository.save(user);

		logger.info("A new user was created with username: {}", user.getUsername());
		return ResponseEntity.ok(user);
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody CreateUserRequest createUserRequest) {

		User user = userRepository.findByUsername(createUserRequest.getUsername());

		if (user == null) {
			logger.warn("User with username {} couldn't be found", createUserRequest.getUsername());
			return ResponseEntity.notFound().build();
		}

		if (!bCryptPasswordEncoder.matches(createUserRequest.getPassword(), user.getPassword())) {

			logger.error("An authorized user access with username {}", createUserRequest.getUsername());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}


		logger.info("User logged in with username: {}", user.getUsername());
		return ResponseEntity.ok(user);
	}
}
