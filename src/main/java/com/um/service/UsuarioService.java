package com.um.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.um.model.Usuario;

public interface UsuarioService {
	
	public Usuario buscaUsuarioPorMatricula(String matricula);
	
	public ArrayList<Usuario> listaUsuarios();
	
	public HashMap<Integer, String> listaUsuarioRoles(int idUsuario);
	
	public void guardarUsuario(Usuario usuario);
	
	public String editarUsuario(Usuario usuario, int[] roles);
	
	public Usuario buscaUsuarioPorId(int id);
	
	public String borrarUsuario(Usuario usuario);
	
	public int doesUserExist(String matricula);
	
	public int changePassword(Usuario usuario);
	
	public int checkRegistrationStatus(Usuario usuario);

	public int RegistrationCompleted(String matricula) ;
	
}
