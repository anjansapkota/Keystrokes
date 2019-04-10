package com.um.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.model.Usuario;
import com.um.service.UsuarioService;

@Controller
public class RegitrarController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@RequestMapping(value={"/register"}, method = RequestMethod.GET)
	public ModelAndView registro(){
		ModelAndView modelAndView = new ModelAndView();
		Usuario usuario = new Usuario();
		modelAndView.addObject("usuario", usuario);
		modelAndView.setViewName("register");
		return modelAndView;
	}
	
	@RequestMapping(value = {"/register"}, method = RequestMethod.POST)
	public ModelAndView crearUsuario(@Valid Usuario usuario, BindingResult bindingResult) {
		
		ModelAndView modelAndView = new ModelAndView();	
		String error = "no";
		int check = usuarioService.doesUserExist(usuario.getMatricula());
		int firstdigit = Integer.parseInt(""+ usuario.getMatricula().charAt(0));
		if(firstdigit == 0) {
			error = "firstlettercantbe0";
			modelAndView.addObject("error", error);
			modelAndView.addObject("usuario", new Usuario());
			modelAndView.setViewName("register");
		} else if (bindingResult.hasErrors()) {
			error = "Si";
			modelAndView.addObject("error", error);
			modelAndView.addObject("usuario", new Usuario());
			modelAndView.setViewName("register");
		}else if(check == 0) {
			usuario.setActivo(1);
			usuarioService.guardarUsuario(usuario);
			modelAndView.addObject("error", error);
			modelAndView.addObject("userRegistertionProblem", "no");
			modelAndView.setViewName("login");
		}else {
			modelAndView.addObject("error", error);
			modelAndView.addObject("usuario", new Usuario());
			modelAndView.addObject("matriculaexiste", check);
			modelAndView.setViewName("register");
			}
		
		return modelAndView;
	}
}