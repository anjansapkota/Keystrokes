package com.um.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.um.model.Role;

@Service("roleService")
public class RoleServiceImpl implements RoleService{
	
	@Autowired
	@Qualifier("postgresJdbcTemplate")
	private JdbcTemplate postgresTemplate;

	@Override
	public ArrayList<Role> listaRoles() {
		ArrayList<Role> roles = new ArrayList<Role>();
		String query = "SELECT * FROM ROLE ORDER BY ROLE";
		postgresTemplate.query(query, new Object[]{}, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {
				Role role = new Role();
				role.setId(rs.getInt("ID"));
				role.setNombre(rs.getString("NOMBRE"));
				role.setDescripcion(rs.getString("DESCRIPCION"));
				roles.add(role);
			}
		});
		return roles;
	}

	@Override
	public void guardarRole(Role role) {
		if(role.getDescripcion().equals("")) {
			role.setDescripcion("-");
		}
		String query = "INSERT INTO ROLE(NOMBRE,DESCRIPCION) VALUES(?, ?)";
		postgresTemplate.update(query, new Object[]{role.getNombre(),role.getDescripcion()});
	}

	@Override
	public String borrarRole(Role role) {
		
		ArrayList<String> tieneUsuarios = new ArrayList<String>();
		
		String borrar 	= "No";
		String query 	= "SELECT * FROM USUARIO_ROLES WHERE ID_ROLE = ?";
		
		postgresTemplate.query(query, new Object[]{role.getId()}, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {
				String usuario = rs.getString("ID_USUARIO");
				if(!usuario.isEmpty()) {
					tieneUsuarios.add("uno");
				}
			}
		});
		
		if(tieneUsuarios.size() > 0) {
			borrar = "No";
		}else {
			query = "DELETE FROM ROLE WHERE ID = ?";
			postgresTemplate.update(query, new Object[]{role.getId()});
			borrar = "Si";
		}
		return borrar;
	}
	
	@Override
	public void editarRole(Role role) {
		String query = "UPDATE ROLE SET NOMBRE = ?, DESCRIPCION = ? WHERE ID = ?";
		postgresTemplate.update(query, new Object[]{role.getNombre(),role.getDescripcion(),role.getId()});
	}

	@Override
	public Role buscaRolePorId(int id) {
		Role role = new Role();
		String query = "SELECT * FROM ROLE WHERE ID = ?";
		postgresTemplate.query(query, new Object[]{id}, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {
				role.setId(rs.getInt("ID"));
				role.setNombre(rs.getString("NOMBRE"));
				role.setDescripcion(rs.getString("DESCRIPCION"));
			}
		});
		return role;
	}

}
