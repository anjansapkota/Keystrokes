package com.um.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.um.model.Key;
import com.um.model.ServiceResponse;

@RestController
public class KeysController {

	List<Key> keys = new ArrayList<>();

	@PostMapping("/refreshA")
	public ResponseEntity<Object> addBook(@RequestBody List<Key> keyscombinations) {
		//method to validate typing
		//String status = "ok";
		@SuppressWarnings("rawtypes")
		ServiceResponse response = new ServiceResponse ();
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping("/refreshB")
	public ResponseEntity<Object> getAllBooks() {
		ServiceResponse<Object> response = new ServiceResponse<>();
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
}
