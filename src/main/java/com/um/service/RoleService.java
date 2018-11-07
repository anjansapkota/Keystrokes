package com.um.service;

import java.util.ArrayList;

import com.um.model.Role;

public interface RoleService {
	
	public ArrayList<Role> listaRoles();

	public void guardarRole(Role role);
	
	public String borrarRole(Role role);
	
	public void editarRole(Role role);
	
	public Role buscaRolePorId(int id);
}
