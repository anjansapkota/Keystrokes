package com.um.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.um.model.Key;
import com.um.model.Usuario;

public class KMSImpl implements KMS {
	@Autowired
	@Qualifier("postgresJdbcTemplate")
	private JdbcTemplate postgresTemplate;

	@Override
	public int personexists(int matricula){
		int personID = 0;
		Vector <Usuario> personas =  new Vector();
		String query = "SELECT * FROM USUARIOS";
		postgresTemplate.query(query, new RowCallbackHandler()	{
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
		
		for (int i = 0; i < personas.size(); i++) {
			if(((personas.get(i)).getMatricula()).equals(matricula)){
				personID = (personas.get(i)).getId();
			}
		}
		return personID;
		
	}
	
	
	

	public void savetoDatabase(int matricula, Vector <Key> KeysList)  throws SQLException{
			int personID=0;
			int pID = personexists(matricula);
			if(pID!=0) {
				personID = pID;
				System.out.println("This is the id of the user that already exists" + personID );
			} else {
				String query = "SELECT MAX(id) as max FROM Usuarios";
				int idUsuario = postgresTemplate.queryForObject(query, Integer.class);
				query = "INSERT INTO tecla (letter1, letter2, press1_release1, press1_press2, release1_press2, release1_release1, userid) VALUES (?, ?, ?, ?, ?)";
				for(int i=0; i<KeysList.size(); i++){
					Key temp = KeysList.get(i);
					Object[] parametros = new Object[]{
							temp.getLetter1(), temp.getLetter2(), temp.getPress1_release1(), temp.getPress1_press2(), temp.getRelease1_press2(), temp.getRelease1_release2(), idUsuario
						};
					postgresTemplate.update(query, parametros);
				}				
			}
	  
	}
	
	public static Vector <Person> bringlistofPersons() throws SQLException{
		Statement stmt 			= null;
		ResultSet rs			= null;
		try {	
			connectDB();
			stmt = connection.createStatement();
			String query1 = "SELECT * FROM person";
		    rs = stmt.executeQuery(query1);
		    while (rs.next()) {
		    	Person personTemp = new Person();
		    	personTemp.setIdperson(rs.getInt("idperson"));
		    	personTemp.setPersonname(rs.getString("personname"));
		    	personas.add(personTemp);
		    }
		} catch (Exception e) {			
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();	
		}finally {
			if (connection!=null) connection.close();
		}
		return personas;
	}
	
	public static int deleteUserData(String name)  throws SQLException{
		int success = 0;
		int personID = 0;
		Statement stmt 			= null;
		ResultSet rs			= null;
		Vector <Person> personas =  new Vector<Person>();
		try {	
			connectDB();
			stmt = connection.createStatement();
			String query1 = "SELECT * FROM person ";
		    rs = stmt.executeQuery(query1);
		    while (rs.next()) {
		    	Person personTemp = new Person();
		    	personTemp.setIdperson(rs.getInt("idperson"));
		    	personTemp.setPersonname(rs.getString("personname"));
		    	if(personTemp.getPersonname().equals("")) {
		    	}else personas.add(personTemp);
		    }
		    
		    for (int i = 0; i < personas.size(); i++) {
				if(((personas.get(i)).getPersonname()).equals(name)){
					personID = (personas.get(i)).getIdperson();
					success=1;
				}
			}
		    
/*		    String query2 = "Delete * FROM person where personname = " + name;
		    stmt.executeQuery(query2);*/
		    
		    String query3 = "DELETE FROM TECLA WHERE PERSONID =" + personID;
		    rs = stmt.executeQuery(query3);
		    
		    success=1;
		    
		} catch (Exception e) {			
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();	
		}finally {
			if (connection!=null) connection.close();
		}
		return success;
	}
	
	
	public static Vector<Tecla> retrieveDBTeclas(int i) throws SQLException {
		
		Statement stmt 			= null;
		ResultSet rs			= null;
		Vector <Tecla> teclasListadeBD =  new Vector <Tecla> ();
		try {	
			connectDB();
			stmt = connection.createStatement();
			String query1 = "SELECT * FROM person";
		    rs = stmt.executeQuery(query1);
		
		    String query2 = "SELECT * FROM tecla where personid =" + i;
		    rs = stmt.executeQuery(query2);
			    while (rs.next()) {
			    	Tecla temp = new Tecla();
			    	temp.letter1 = rs.getString("letter1");
			    	temp.letter2 = rs.getString("letter2");
			    	temp.time_diff = rs.getLong("time_diff");
			    	temp.duration = rs.getLong("duration");
			    	teclasListadeBD.add(temp);
			    }

			} catch (Exception e) {			
				System.out.println("Where is your PostgreSQL JDBC Driver? "
						+ "Include in your library path!");
				e.printStackTrace();	
			}finally {
				if (connection!=null) connection.close();
			}
		
		return teclasListadeBD;
		}

}
