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


		double standardDeV(double[] sample);

	double Mean(double[] sample);

	double checkNormalDistribution(double[] sample, double lowerbound, double upperbound);

	double distributionanalysis(IndividialDigraphsSet ids);

	Vector<Object> bringAllData(Result ResultTable) throws SQLException;

	Result tTest(Result ResultTable, Vector<Key> teclaList1, Vector<Key> teclaList2, String Name);

	Result checkMatches(Result ResultTable, Vector<Key> persona1Summary, Vector<Key> personaPruebaSummary, String Name);

	Result correlationtest(Result ResultTable, Vector<Key> teclaList1, Vector<Key> teclaList2, String Name);
}