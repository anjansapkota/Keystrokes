package com.um.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.um.model.Usuario;

@Service("usuarioService")
public class UsuarioServiceImpl implements UsuarioService {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	 @Autowired
		@Qualifier("postgresJdbcTemplate")
		private JdbcTemplate postgresTemplate;

	@Override
	public Usuario buscaUsuarioPorMatricula(String matricula) {
		Usuario usuario = new Usuario();
    	String query = "SELECT * FROM USUARIO WHERE MATRICULA = ?";
    	postgresTemplate.query(query, new Object[]{matricula}, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {		
				usuario.setId(rs.getInt("ID"));
				usuario.setNombre(rs.getString("NOMBRE"));
				usuario.setMatricula(rs.getString("MATRICULA"));
				usuario.setPassword(rs.getString("PASSWORD"));
				usuario.setActivo(rs.getInt("ACTIVO"));								
		   }
		});
    	return usuario;
	}
	
	@Override
	public int doesUserExist(String matricula) {
		int result = 0;
		Usuario usuario = new Usuario();
    	String query = "SELECT * FROM USUARIO WHERE MATRICULA = ?";
    	postgresTemplate.query(query, new Object[]{matricula}, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {		
				usuario.setId(rs.getInt("ID"));
				usuario.setNombre(rs.getString("NOMBRE"));
				usuario.setMatricula(rs.getString("MATRICULA"));
				usuario.setPassword(rs.getString("PASSWORD"));
				usuario.setActivo(rs.getInt("ACTIVO"));								
		   }
		});
    	if(matricula.equals(usuario.getMatricula())) {
    		System.out.println("The Matricula Already Exists");
    		result=1;
    		} else System.out.println("******* The Matricula Doesn't Exists");
    	return result;
	}
	
	

	@Override
	public ArrayList<Usuario> listaUsuarios() {
		ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
		String query = "SELECT * FROM USUARIO ORDER BY NOMBRE";
		postgresTemplate.query(query, new Object[]{}, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {
				Usuario usuario = new Usuario();	
				usuario.setId(rs.getInt("ID"));
				usuario.setNombre(rs.getString("NOMBRE"));
				usuario.setMatricula(rs.getString("MATRICULA"));
				usuario.setPassword(rs.getString("PASSWORD"));
				usuario.setActivo(rs.getInt("ACTIVO"));								
				usuarios.add(usuario);
		   }
		});
		return usuarios;
	}

	@Override
	public void guardarUsuario(Usuario usuario) {
		usuario.setPassword(bCryptPasswordEncoder.encode(usuario.getPassword()));
		String query = "INSERT INTO USUARIO(NOMBRE,MATRICULA,PASSWORD,ACTIVO) VALUES(?, ?, ?, 1)";
		
		Object[] parametros = new Object[]{
			usuario.getNombre(),usuario.getMatricula(),usuario.getPassword()
		};
		
		postgresTemplate.update(query, parametros);
		
		query = "SELECT MAX(ID) FROM USUARIO";
		int idUsuario = postgresTemplate.queryForObject(query, Integer.class);
		query = "INSERT INTO usuario_roles(ID_USUARIO, ID_ROLE) VALUES (?, ?)";
		parametros = new Object[]{idUsuario,1};
		postgresTemplate.update(query, parametros);
	}

	@Override
	public String editarUsuario(Usuario usuario, int[] roles) {
		String query = " UPDATE USUARIO SET NOMBRE = ?, MATRICULA = ? WHERE ID = ?";
			Object[] parametros = new Object[] {
					usuario.getNombre(),usuario.getMatricula(),usuario.getId()
			};
		
		postgresTemplate.update(query, parametros);
		
		query = "DELETE FROM roles WHERE ID_USUARIO = ?";
		postgresTemplate.update(query, new Object[]{usuario.getId()});
		
		ArrayList<String> listRoles = new ArrayList<String>();
		
		for(int idRole : roles) {
			query = "SELECT NOMBRE FROM ROLE WHERE ID = ?";
			postgresTemplate.query(query, new Object[]{idRole}, new RowCallbackHandler()	{
				public void processRow(ResultSet rs) throws SQLException {	
					listRoles.add(rs.getString("NOMBRE"));
				}
			});
			query = "INSERT INTO roles(ID_USUARIO, ID_ROLE) VALUES( ?, ?)";
			postgresTemplate.update(query, new Object[]{usuario.getId(),idRole});
		}
		
		String edito = "si";
		return edito;
	}

	@Override
	public Usuario buscaUsuarioPorId(int id) {
		Usuario usuario = new Usuario();
		String query = "SELECT * FROM USUARIO WHERE ID = ?";
		postgresTemplate.query(query, new Object[]{id}, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {
				usuario.setId(rs.getInt("ID"));
				usuario.setNombre(rs.getString("NOMBRE"));
				usuario.setMatricula(rs.getString("MATRICULA"));
				usuario.setPassword(rs.getString("PASSWORD"));
				usuario.setActivo(rs.getInt("ACTIVO"));								
		   }
		});
		return usuario;
	}

	@Override
	public HashMap<Integer, String> listaUsuarioRoles(int idUsuario) {
		HashMap<Integer, String> mapRolesUsuario = new HashMap<Integer, String>();
		String query = "SELECT * FROM ROLE WHERE ID IN(SELECT ID_ROLE FROM roles WHERE ID_USUARIO = ?)";
		postgresTemplate.query(query, new Object[]{idUsuario}, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {	
				mapRolesUsuario.put(rs.getInt("ID"), rs.getString("NOMBRE"));
			}
		});
		return mapRolesUsuario;
	}

	@Override
	public String borrarUsuario(Usuario usuario) {
		String query = "DELETE FROM USUARIO WHERE ID = ?";
		postgresTemplate.update(query, new Object[]{usuario.getId()});
		
		query = "DELETE FROM roles WHERE ID_USUARIO = ?";
		postgresTemplate.update(query, new Object[]{usuario.getId()});
		
		String borrar = "Si";
		return borrar;
	}
	
	
	@Override
	public int changePassword(Usuario usuario) {
		usuario.setPassword(bCryptPasswordEncoder.encode(usuario.getPassword()));
		String query = " UPDATE USUARIO SET PASSWORD = ? WHERE ID = ?";
			Object[] parametros = new Object[] {
					usuario.getPassword(),usuario.getId()
			};
		int a = postgresTemplate.update(query, parametros);
		return a;
	}
	
	@Override
	public int checkRegistrationStatus(Usuario usuario) {
		int a = 0;
		String query = "SELECT registration_completed FROM usuario WHERE matricula = ?";
			Object[] parametros = new Object[] {
					usuario.getMatricula()
			};
			postgresTemplate.query(query, parametros, new RowCallbackHandler()	{
				public void processRow(ResultSet rs) throws SQLException {
					usuario.setReg_estado(rs.getInt("registration_completed"));
			   }
			});
			if(usuario.getReg_estado() == 1001) {
				a=1;
			}	
		return a;
	}
	
	@Override
	public int RegistrationCompleted(String matricula) {
		int stat= 1001;
		int a = 0;
		String query = "UPDATE USUARIO SET registration_completed = ? WHERE matricula = ? ";
			Object[] parametros = new Object[] {
					stat, matricula
			};
			a = postgresTemplate.update(query, parametros);
		return a;
	}

}
