package com.um.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import com.um.model.IndividialDigraphsSet;
import com.um.model.Key;
import com.um.model.Result;

public interface LaboratoryService {
	void printVector(Vector<Key> printingList, String name);

	int checkIfCopyPasted(Vector<Key> listofKeysRecieved);

	Vector<Object> bringAllData(HashMap<String, Result> ResultTable) throws SQLException;

	HashMap<String, Result> tTest(HashMap<String, Result> ResultTable, Vector<Key> teclaList1, Vector<Key> teclaList2,
			String Name);

	HashMap<String, Result> checkMatches(HashMap<String, Result> ResultTable, Vector<Key> persona1Summary,
			Vector<Key> personaPruebaSummary, String Name);

	HashMap<String, Result> correlationtest(HashMap<String, Result> ResultTable, Vector<Key> teclaList1, Vector<Key> teclaList2,
			String Name);

	HashMap<String, Result> MapResultsOfUsers(HashMap<String, Result> ResultTable);

		double standardDeV(double[] sample);

	double Mean(double[] sample);

	double checkNormalDistribution(double[] sample, double lowerbound, double upperbound);

	double distributionanalysis(HashMap<String, IndividialDigraphsSet> digraphsCollection);
}