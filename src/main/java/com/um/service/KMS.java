package com.um.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import com.um.model.Key;
import com.um.model.Role;
import com.um.model.Usuario;

public interface KMS {
	int personexists(String matricula);

	Vector<Key> retrieveKeysFromDB(String matricula) throws SQLException;

	int deleteUserData(String matricula) throws SQLException;

	Vector<Usuario> bringlistofPersons();

	int savetoDatabase(String matricula, Vector<Key> KeysList) throws SQLException;
}