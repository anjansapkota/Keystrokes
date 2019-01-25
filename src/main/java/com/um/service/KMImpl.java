package com.um.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.um.model.Key;
import com.um.model.Usuario;
@Service("kmService")
public class KMImpl implements KmService {
	 @Autowired
		@Qualifier("postgresJdbcTemplate")
		private JdbcTemplate postgresTemplate;
	 @Autowired
	 UsuarioService us;
	@Override
	public int personexists(String matricula){
		int personID=0;
		ArrayList <Usuario> personas = us.listaUsuarios();
		for (int i = 0; i < personas.size(); i++) {
			if(((personas.get(i)).getMatricula()).equals(matricula)){
				personID = (personas.get(i)).getId();
			}
		}
		return personID;
		
	}
	
	
	
	@Override
	public int savetoDatabase(String matricula, Vector <Key> KeysList)  throws SQLException{
			int a = 0;
			int pID = personexists(matricula);
			String query = "INSERT INTO tecla (letter1, letter2, press1_release1, press1_press2, release1_press2, release1_release2, userid) VALUES (?, ?, ?, ?, ?, ?, ?)";
				for(int i=0; i<KeysList.size(); i++){
					Key temp = KeysList.get(i);
					Object[] parametros = new Object[]{
							temp.getLetter1(), temp.getLetter2(), temp.getPress1_release1(), temp.getPress1_press2(), temp.getRelease1_press2(), temp.getRelease1_release2(), pID};
					a = postgresTemplate.update(query, parametros);
				}
				return a;
			}
	@Override
	public Vector <Usuario> bringlistofPersons(){
		Vector <Usuario> personas =  new Vector();
		String query = "SELECT * FROM USUARIO";
		postgresTemplate.query(query, new Object[]{}, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {		
				while (rs.next()) {
			    	Usuario personTemp = new Usuario();
			    	personTemp.setId(rs.getInt("ID"));
			    	personTemp.setNombre(rs.getString("NOMBRE"));
			    	personTemp.setMatricula(rs.getString("MATRICULA"));
			    	personas.add(personTemp);
			    }							
		   }
		});
		return personas;
	}
	@Override
	public int deleteUserData(String matricula)  throws SQLException{
		int pID = personexists(matricula);
		int a = 0;
		String query = "DELETE FROM TECLA WHERE userid = ?";
		Object[] parametros = new Object[]{pID};
		a = postgresTemplate.update(query, parametros);
		return a;
	}
	
	@Override
	public Vector<Key> retrieveKeysFromDB(String matricula) throws SQLException {
		Vector <Key> teclasListadeBD =  new Vector <Key> ();
		String query = "SELECT * FROM tecla where userid =" + personexists(matricula);
		postgresTemplate.query(query, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {		
				while (rs.next()) {
			    	Key temp = new Key();
			    	temp.setId(rs.getInt("ID"));
			    	temp.setLetter1(rs.getString("letter1"));
			    	temp.setLetter2(rs.getString("letter2"));
			    	temp.setPress1_release1(rs.getLong("press1_release1"));
			    	temp.setPress1_press2(rs.getLong("press1_press2"));
			    	temp.setRelease1_press2(rs.getLong("release1_press2"));
			    	temp.setRelease1_release2(rs.getLong("release1_release2"));
			    	teclasListadeBD.add(temp);
			    }							
		   }
		});
		
		return teclasListadeBD;
		}
	
	
	@Override
	public int deleteRepeatedKeys(String matricula)  throws SQLException{
		Vector<Key> retrieveKeysFromDB = retrieveKeysFromDB(matricula);
		String query = "DELETE FROM TECLA WHERE id = ?";
		int a, count =0;
		for (int i = 0; i < retrieveKeysFromDB.size(); i++) {
			Key temp1 = retrieveKeysFromDB.get(i);
			int b = 0;
			if(i+1 < retrieveKeysFromDB.size()) {
				Key temp2 = retrieveKeysFromDB.get(i+1);
				if(temp1.getLetter1().equals(temp2.getLetter1()) && temp1.getLetter2().equals(temp2.getLetter2()) && temp1.getPress1_press2()== temp2.getPress1_press2()) {
					Object[] parametros = new Object[]{temp2.getId()};
					a = postgresTemplate.update(query, parametros);
					count = count+a;
				}
			}
		}
		return count;
	}

}
