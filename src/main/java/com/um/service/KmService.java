package com.um.service;

import java.sql.SQLException;
import java.util.Vector;

import com.um.model.Key;
import com.um.model.Usuario;

public interface KmService {
	int personexists(String matricula);

	Vector<Key> retrieveKeysFromDB(String matricula) throws SQLException;

	int deleteUserData(String matricula) throws SQLException;

	Vector<Usuario> bringlistofPersons();

	int savetoDatabase(String matricula, Vector<Key> KeysList) throws SQLException;

	int deleteRepeatedKeys(String matricula) throws SQLException;
}