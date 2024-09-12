package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private final static Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		log.info("attempting to submit order for user: {}", username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.info("submit order request failed, Reason: user not found");
			return ResponseEntity.notFound().build();
		}
		Cart cart = user.getCart();
		if(cart == null || cart.getItems().isEmpty()) {
			log.info("submit order request failed, Reason: cart is empty");
			return ResponseEntity.badRequest().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		user.setCart(new Cart());
		userRepository.save(user);
		log.info("order places successfully for user: {}, response code: 200", username);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		log.info("attempting to get order history for user: {}", username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.info("get order history request failed, Reason: user not found");
			return ResponseEntity.notFound().build();
		}
		log.info("order history retrieved successfully for user: {}, response code: 200", username);
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
