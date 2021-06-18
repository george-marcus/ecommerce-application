package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RequestMapping("/api/item")
public class ItemController extends BaseController{

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {

		List<Item> items = itemRepository.findAll();

		if(items.isEmpty()){

			logger.warn("No item was found on item repository");
			return ResponseEntity.notFound().build();
		}

		logger.info("Found a list of all items");
		return ResponseEntity.ok(items);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		Optional<Item> item = itemRepository.findById(id);

		if(!item.isPresent()){

			logger.warn("No item with id {} was found", id);
			return ResponseEntity.notFound().build();
		}

		logger.info("An item with id {} was found", id);
		return ResponseEntity.ok(item.get());
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {

		List<Item> items = itemRepository.findByName(name);

		if(items == null || items.isEmpty() ){

			logger.warn("No item with name {} was found", name);
			return ResponseEntity.notFound().build();
		}

		logger.info("A list of all item with name {} was found", name);
		return ResponseEntity.ok(items);
			
	}
	
}
