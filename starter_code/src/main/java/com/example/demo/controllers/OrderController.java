package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RequestMapping("/api/order")
public class OrderController extends BaseController{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {

		User user = userRepository.findByUsername(username);
		if(user == null) {

			logger.warn("User {} couldn't be found", username);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);

		logger.info("An order of {} has been submitted", username);

		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {

		User user = userRepository.findByUsername(username);

		if(user == null) {
			logger.warn("User {} couldn't be found", username);
			return ResponseEntity.notFound().build();
		}

		List<UserOrder> userOrders = orderRepository.findByUser(user);

		if(userOrders.isEmpty()){
			logger.warn("User {} doesn't have orders yet", user.getUsername());
			return ResponseEntity.notFound().build();
		}

		logger.info("A list of orders for username: {} were found", user.getUsername());
		return ResponseEntity.ok(userOrders);
	}
}
