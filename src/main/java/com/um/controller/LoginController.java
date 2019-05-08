package com.um.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.model.Usuario;
import com.um.service.UsuarioService;

@Controller
public class LoginController {
	@Autowired
	private UsuarioService usuarioService;
	
	@RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	
	@RequestMapping(value={"/logout"}, method = RequestMethod.GET)
	public static void  logout(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return ;
	}
	
	@RequestMapping(value="/inicio", method = RequestMethod.GET)
	public ModelAndView inicio(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.buscaUsuarioPorMatricula(auth.getName());
		modelAndView.addObject("user", usuario.getNombre());
		modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
		if(usuarioService.checkRegistrationStatus(usuario)==1) {			
			modelAndView.setViewName("home");
		}
		else {
			modelAndView.addObject("reg_estado", "incompleto");
			modelAndView.setViewName("registrationDetails");
		}
		
		return modelAndView;
	}
	
	@RequestMapping(value={"/changePassword"}, method = RequestMethod.GET)
	public ModelAndView changePasswordRequest(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.buscaUsuarioPorMatricula(auth.getName());
		modelAndView.addObject("user", usuario.getNombre());
		modelAndView.addObject("requestingPasswordChange", "Si");
		modelAndView.setViewName("home");
		return modelAndView;
	}

	@RequestMapping(value= {"/SdpmSt204snirkdT/5S4dkas2dkasdSD/{password}/aFrt6nalResaW3qw"}, method = RequestMethod.POST)
	public  ResponseEntity<Object> changePass(@PathVariable String password){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.buscaUsuarioPorMatricula(auth.getName());
		usuario.setPassword(password);
		int a = usuarioService.changePassword(usuario);
		return new ResponseEntity<Object>(a, HttpStatus.OK);
	}
}
