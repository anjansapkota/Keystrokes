package com.um.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.um.model.Key;
import com.um.model.Usuario;
import com.um.service.KMS;

@RestController
public class KeysController {
	Vector <Key> listofKeysRecieved = new Vector<>();
	List<JSONObject> keys = new ArrayList<>();
	private Authentication auth;
	private KMS kms;
	@RequestMapping(value= {"/refresh"}, method = RequestMethod.POST)
	public ResponseEntity<Object> refresh(@RequestBody String json) throws JSONException, ParseException {
		//method to validate typing
		JSONParser parser = new JSONParser();
		keys = (List<JSONObject>) parser.parse(json);
		for (int i = 0; i < keys.size(); i++) {
			Key tempkey = new Key();
			tempkey.setLetter1((String) keys.get(i).get("l1"));  //"l1":KM, "l2":KN, "p1r1":A, "p1r1":B, "r1p2":C, "r1r2":D -- this is how it is in javascript in view
			tempkey.setLetter2((String) keys.get(i).get("l2"));
			tempkey.setPress1_press2((long) keys.get(i).get("p1p2"));
			tempkey.setPress1_release1((long) keys.get(i).get("p1r1"));
			tempkey.setRelease1_press2((long) keys.get(i).get("r1p2"));
			tempkey.setRelease1_release2((long) keys.get(i).get("r1r2"));
			listofKeysRecieved.add(tempkey);
		}
		int response = 1;
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	public void saveKeys(List<Key> listofKeys, Usuario user) {
		
	}

	@RequestMapping(value= {"/reg_complete"}, method = RequestMethod.POST)
	public void regComplete() throws SQLException {
		auth = SecurityContextHolder.getContext().getAuthentication();
		String matriculaEnTexto = auth.getName();
		kms.savetoDatabase(matriculaEnTexto, listofKeysRecieved);
		LoginController.logout();
		ModelAndView modelAndView = new ModelAndView();
		String error = "no";
		modelAndView.addObject("error", error);
		modelAndView.addObject("userRegistertionProblem", "no");
		modelAndView.setViewName("login");
	}
}
