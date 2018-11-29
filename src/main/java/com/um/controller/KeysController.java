package com.um.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.um.model.Key;

@RestController
public class KeysController {

	List<Key> keys = new ArrayList<>();
	@RequestMapping(value= {"/refresh"}, method = RequestMethod.POST)
	public ResponseEntity<Object> addBook(@RequestBody String json) throws JSONException, ParseException {
		//method to validate typing
		JSONParser parser = new JSONParser();
		keys = (List<Key>) parser.parse(json);
		int response = 1;
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	
}
