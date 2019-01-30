package com.um.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.validation.Valid;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.um.model.Key;
import com.um.model.Result;
import com.um.model.Usuario;
import com.um.service.KmService;
import com.um.service.LaboratoryService;
import com.um.service.UsuarioService;

@RestController
public class KeysManagementController {
	
	@Autowired
	private UsuarioService usuarioService;	
	@Autowired
	private LaboratoryService lbs;
	@Autowired
	private KmService kmService;
	Vector <Key> listofKeysRecieved = new Vector<>();
	Vector <Key> listofKeysExpectedUser = new Vector<>();
	List<JSONObject> keys = new ArrayList<>();
	HashMap<String, Result> ResultTable = new HashMap<String, Result>();
	private Authentication auth;	
	private int key = 0; //to avoid error 500 for refresh
	
	//reveiving keys to validate the user
	@RequestMapping(value= {"/refresh"}, method = RequestMethod.POST)
	public ResponseEntity<Object> refresh(@RequestBody String json) throws JSONException, ParseException {
		//method to validate typing
		JSONParser parser = new JSONParser();
		keys = (List<JSONObject>) parser.parse(json);
		if(keys.size()<=7) {
			listofKeysRecieved= new Vector<Key>();
			ResultTable = new HashMap<String, Result>();
		}
		for (int i = 0; i < keys.size(); i++) {					//converting json array to model.key array
			Key tempkey = new Key();
			if(key > 0) {
				tempkey.setLetter1((String) keys.get(i).get("l1"));  //"l1":KM, "l2":KN, "p1r1":A, "p1r1":B, "r1p2":C, "r1r2":D -- this is how it is in javascript in view
				tempkey.setLetter2((String) keys.get(i).get("l2"));
				tempkey.setPress1_press2((long) keys.get(i).get("p1p2"));
				tempkey.setPress1_release1((long) keys.get(i).get("p1r1"));
				tempkey.setRelease1_press2((long) keys.get(i).get("r1p2"));
				tempkey.setRelease1_release2((long) keys.get(i).get("r1r2"));
				listofKeysRecieved.add(tempkey);				
			}
			key = 1;
		}
		
		if(listofKeysRecieved.size()%100<9) {			//to test every 100 keys
		auth = SecurityContextHolder.getContext().getAuthentication();
		String matriculaEnTexto = auth.getName();
		try {
			listofKeysExpectedUser = kmService.retrieveKeysFromDB(matriculaEnTexto);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ResultTable = lbs.MapResultsOfUsers(ResultTable);
		ResultTable = lbs.checkMatches(ResultTable, listofKeysRecieved, listofKeysExpectedUser, matriculaEnTexto);
		System.out.println("The user was matched " + ResultTable.get(matriculaEnTexto).getMatchesResult() + "Times.");
		}
		int response = 1;
		int aaa  = lbs.checkIfCopyPasted(listofKeysRecieved);
		if(aaa == 1) {
			response=2;
		}
		
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	public void saveKeys(List<Key> listofKeys, Usuario user) {
		
	}

	@RequestMapping(value= {"/reg_complete"}, method = RequestMethod.POST)
	public ModelAndView regComplete(String textWhyChose, String textEssay) throws SQLException {
		ModelAndView modelAndView = new ModelAndView();
		String view = "";
		auth = SecurityContextHolder.getContext().getAuthentication();
		String matriculaEnTexto = auth.getName();
		int aaa  = lbs.checkIfCopyPasted(listofKeysRecieved);
		System.out.println(aaa);
		if(aaa==0){
			kmService.savetoDatabase(matriculaEnTexto, listofKeysRecieved);
			usuarioService.RegistrationCompleted(matriculaEnTexto);
			modelAndView.addObject("regcomplete", "si");
			view = "home";
		}else {
			modelAndView.addObject("copied", "si");
			view = "registrationDetails";
		}
		listofKeysRecieved = null;
		modelAndView.setViewName(view);
		return modelAndView;
	}
}