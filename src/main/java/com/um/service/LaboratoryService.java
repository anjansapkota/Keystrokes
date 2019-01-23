package com.um.service;

import java.sql.SQLException;
import java.util.Vector;

import com.um.model.Key;

public interface LaboratoryService {

	Vector<Object> bringAllData() throws SQLException;

	void printVector(Vector<Key> printingList, String name);

	void tTest(Vector<Key> teclaList1, Vector<Key> teclaList2, String Name);

	int checkIfCopyPasted(Vector<Key> listofKeysRecieved);

	void checkMatches(Vector<Key> persona1Summary, Vector<Key> personaPruebaSummary, String Name);

	void correlationtest(Vector<Key> teclaList1, Vector<Key> teclaList2, String Name);
}