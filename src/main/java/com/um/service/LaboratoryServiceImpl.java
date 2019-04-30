package com.um.service;

import java.awt.BorderLayout;
import java.io.File;
import java.io.PrintWriter;
import java.sql.SQLException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.um.model.Corelation;
import com.um.model.IndividialDigraphsSet;
import com.um.model.Key;
import com.um.model.Result;
import com.um.model.T_Test;
import com.um.model.Usuario;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.OneClassClassifier;

@Service("laboratoryService")
public class LaboratoryServiceImpl implements LaboratoryService {
	@Autowired
	private KmService kmService;
	// @Autowired
	// private VisualService vs;
	@Autowired
	UsuarioService us;
	private Vector<Object> keys4mdbEvry1 = new Vector<Object>();
	private static long P1R1 = 800;
	private static long P1P2 = 800;
	private static long R1P2 = 800;
	private static long R1R2 = 800;
	private static long P1R2 = 1600;
	private String name = "";
	public String result = "";

	@Override
	public Vector<Object> bringAllData(Result ResultTable) throws SQLException {
		Vector<Object> AllData = new Vector<Object>();
		Vector<Usuario> personas = kmService.bringlistofPersons();
		Vector<String> userNames = new Vector<String>();
		for (int i = 0; i < personas.size(); i++) {
			Vector<Key> registrosTemp = kmService.retrieveKeysFromDB(personas.get(i).getMatricula());
			for (int jj = 0; jj < registrosTemp.size(); jj++) {
				if (registrosTemp.get(jj).getRelease1_press2() >= 50000) {
					registrosTemp.remove(jj);
				}
			}
			keys4mdbEvry1.add(registrosTemp);
			printVector(registrosTemp, personas.get(i).getNombre());
			userNames.add(personas.get(i).getNombre());
		}
		AllData.add(keys4mdbEvry1);
		AllData.add(userNames);
		return AllData; // All data is an array of objects, where the first object is the array of list
						// of keycombinations of all the users from the database and the second is
						// the usernames of all the users.
	}

	@Override
	public void printVector(Vector<Key> printingList, String name) {
		System.out.println();
		System.out.println("The vector " + name + " is printing below.");
		for (int i = 0; i < printingList.size(); i++) {
			Key temp = printingList.get(i);
			System.out.println(temp.getLetter1() + ", " + temp.getLetter2() + ", " + temp.getPress1_release1() + ", "
					+ temp.getPress1_press2() + ", " + temp.getRelease1_press2() + ", " + temp.getRelease1_release2() + ", " + temp.getPress1_release2());
		}
		System.out.println("The vector " + name + " finished printing");
		System.out.println();
	}

	@Override
	public Result tTest(Result ResultTable, Vector<Key> teclaList1, Vector<Key> teclaList2, String Name) {
		double[] sample1A = new double[teclaList1.size()];
		double[] sample1B = new double[teclaList1.size()];
		double[] sample1C = new double[teclaList1.size()];
		double[] sample1D = new double[teclaList1.size()];
		double[] sample1E = new double[teclaList1.size()];
		for (int i = 0; i < teclaList1.size(); i++) {
			sample1A[i] = teclaList1.get(i).getPress1_release1();
			sample1B[i] = teclaList1.get(i).getPress1_press2();
			sample1C[i] = teclaList1.get(i).getRelease1_press2();
			sample1D[i] = teclaList1.get(i).getRelease1_release2();
			sample1E[i] = teclaList1.get(i).getPress1_release2();
		}

		double[] sample2A = new double[teclaList2.size()];
		double[] sample2B = new double[teclaList2.size()];
		double[] sample2C = new double[teclaList2.size()];
		double[] sample2D = new double[teclaList2.size()];
		double[] sample2E = new double[teclaList2.size()];
		for (int i = 0; i < teclaList2.size(); i++) {
			sample2A[i] = teclaList2.get(i).getPress1_release1();
			sample2B[i] = teclaList2.get(i).getPress1_press2();
			sample2C[i] = teclaList2.get(i).getRelease1_press2();
			sample2D[i] = teclaList2.get(i).getRelease1_release2();
			sample2E[i] = teclaList2.get(i).getPress1_release2();
		}
		T_Test tTestResult = new T_Test();
		double pValuePress1_release1;
		double pValuePress1_press2;
		double pValueRelease1_press2;
		double pValueRelease1_release2;
		double pValuePress1_release2;
		DecimalFormat df = new DecimalFormat("#.00000");
		TTest ttest = new TTest();
		pValuePress1_release1 = Double.parseDouble(df.format(ttest.tTest(sample1A, sample2A)));
		pValuePress1_press2 = Double.parseDouble(df.format(ttest.tTest(sample1B, sample2B)));
		pValueRelease1_press2 = Double.parseDouble(df.format(ttest.tTest(sample1C, sample2C)));
		pValueRelease1_release2 = Double.parseDouble(df.format(ttest.tTest(sample1D, sample2D)));
		pValuePress1_release2 = Double.parseDouble(df.format(ttest.tTest(sample1E, sample2E)));
		tTestResult.setA(pValuePress1_release1);
		tTestResult.setB(pValuePress1_press2);
		tTestResult.setC(pValueRelease1_press2);
		tTestResult.setD(pValueRelease1_release2);
		tTestResult.setE(pValuePress1_release2);
		ResultTable.settTestResult(tTestResult);		
		result = result + "   " + (
				"T-Test Results : " + "P1R1: " + pValuePress1_release1 + " and " + "P1P2: " + pValuePress1_press2
						+ " and " + "R1P2: " + pValueRelease1_press2 + " and " + "R1R2: " + pValueRelease1_release2 + " and " + "P1R2: " + pValuePress1_release2);
		return ResultTable;

	}

	@Override
	public Result checkMatches(Result ResultTable, Vector<Key> persona1Summary, Vector<Key> personaPruebaSummary,
			String Name) throws Exception {
		result ="Results: ";
		name = Name;
		double poss_by_class = classify(persona1Summary, personaPruebaSummary);
		// HashMap<String, Object> IPD = new HashMap<String, Object> (); //individual or
		// unique_pair_diagraph
		int matchcount = 0;
		int N_sample = 0;
		HashMap<Long, IndividialDigraphsSet> collectionPD = new HashMap<Long, IndividialDigraphsSet>(); // collection_of_paired_diagraphs_of_both_persons
		Vector<Long> digraphsList = new Vector<Long>();
		Vector<Key> teclasIgualespersonaPrueba = new Vector<Key>();
		Vector<Key> teclasIgualesPersona1 = new Vector<Key>();
		Vector<Key> commonKeysPrueba = new Vector<Key>();
		Vector<Key> commonKeysPersona1 = new Vector<Key>();
		for (int i = 1; i < personaPruebaSummary.size(); i++) { // Prueba is i //person is j
			int confirmation = 0;
			long l1 = personaPruebaSummary.get(i).getLetter1();
			long l2 = personaPruebaSummary.get(i).getLetter2();
			for (int j = 0; j < persona1Summary.size(); j++) {
				if (persona1Summary.get(j).getLetter1() == l1 && persona1Summary.get(j).getLetter2() == l2) {
					N_sample++;
					// no matter the value of any variables, its a set of collections of same
					// recurring digraphs
					Long digraph = l1 + l2;
					if (!collectionPD.containsKey(digraph)) {
						Vector<Key> tempkeys1 = new Vector<Key>();
						Vector<Key> tempkeys2 = new Vector<Key>();
						IndividialDigraphsSet ids = new IndividialDigraphsSet();
						tempkeys1.add(personaPruebaSummary.get(i));
						tempkeys2.add(persona1Summary.get(j));
						digraphsList.add(digraph);
						ids.setDigraph(digraph);
						ids.setPrueba(tempkeys1);
						ids.setPerson(tempkeys2);
						collectionPD.put(digraph, ids);
						commonKeysPrueba.add(personaPruebaSummary.get(i));
						commonKeysPersona1.add(persona1Summary.get(j));
						confirmation = 1;
					} else if (confirmation == 0) { // to avoid duplication of keys(model) while sorting out common
													// digraphs.
						collectionPD.get(digraph).getPrueba().add(personaPruebaSummary.get(i));
						confirmation = 1;
						commonKeysPrueba.add(personaPruebaSummary.get(i));
					}
					collectionPD.get(digraph).getPerson().add(persona1Summary.get(j));
					commonKeysPersona1.add(persona1Summary.get(j));

					// Matching Analysis and sorting out common key press
					if ((personaPruebaSummary.get(i).getPress1_release1() > persona1Summary.get(j).getPress1_release1())
							&& (personaPruebaSummary.get(i).getPress1_release1()
									- persona1Summary.get(j).getPress1_release1() <= P1R1)
							|| (personaPruebaSummary.get(i).getPress1_release1() < persona1Summary.get(j)
									.getPress1_release1())
									&& (persona1Summary.get(j).getPress1_release1()
											- personaPruebaSummary.get(i).getPress1_release1() <= P1R1)) {
						matchcount++;
						// System.out.println("Found match where i .. " + i +" j .. " + j + " " + "
						// KEYID " + persona1Summary.get(j).getId() + " " + l1 + " " + l2 + "
						// Press1_release1 " + (personaPruebaSummary.get(i).getPress1_release1() -
						// persona1Summary.get(j).getPress1_release1()));
						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
						teclasIgualesPersona1.add(persona1Summary.get(j));
					}

					if ((personaPruebaSummary.get(i).getPress1_press2() > persona1Summary.get(j).getPress1_press2())
							&& (personaPruebaSummary.get(i).getPress1_press2()
									- persona1Summary.get(j).getPress1_press2() <= P1P2)
							|| (personaPruebaSummary.get(i).getPress1_press2() < persona1Summary.get(j)
									.getPress1_press2())
									&& (persona1Summary.get(j).getPress1_press2()
											- personaPruebaSummary.get(i).getPress1_press2() <= P1P2)) {
						matchcount++;
						// System.out.println("Found match where i .. " + i +" j .. " + j + " " + "
						// KEYID " + persona1Summary.get(j).getId() + " " + l1 + " " + l2 + "
						// Press1_press2 " + (personaPruebaSummary.get(i).getPress1_press2() -
						// persona1Summary.get(j).getPress1_press2()));
						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
						teclasIgualesPersona1.add(persona1Summary.get(j));
					}

					if ((personaPruebaSummary.get(i).getRelease1_press2() > persona1Summary.get(j).getRelease1_press2())
							&& (personaPruebaSummary.get(i).getRelease1_press2()
									- persona1Summary.get(j).getRelease1_press2() <= R1P2)
							|| (personaPruebaSummary.get(i).getRelease1_press2() < persona1Summary.get(j)
									.getRelease1_press2())
									&& (persona1Summary.get(j).getRelease1_press2()
											- personaPruebaSummary.get(i).getRelease1_press2() <= R1P2)) {
						matchcount++;
						// System.out.println("Found match where i .. " + i +" j .. " + j + " " + "
						// KEYID " + persona1Summary.get(j).getId() + " " + l1 + " " + l2 + "
						// Release1_press2 " + (personaPruebaSummary.get(i).getRelease1_press2() -
						// persona1Summary.get(j).getRelease1_press2()));
						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
						teclasIgualesPersona1.add(persona1Summary.get(j));
					}

					if ((personaPruebaSummary.get(i).getRelease1_release2() > persona1Summary.get(j)
							.getRelease1_release2())
							&& (personaPruebaSummary.get(i).getRelease1_release2()
									- persona1Summary.get(j).getRelease1_release2() <= R1R2)
							|| (personaPruebaSummary.get(i).getRelease1_release2() < persona1Summary.get(j)
									.getRelease1_release2())
									&& (persona1Summary.get(j).getRelease1_release2()
											- personaPruebaSummary.get(i).getRelease1_release2() <= R1R2)) {
						matchcount++;
						// System.out.println("Found match where i .. " + i +" j .. " + j + " " + "
						// KEYID " + persona1Summary.get(j).getId() + " " + l1 + " " + l2 + "
						// Release1_release2 " + (personaPruebaSummary.get(i).getRelease1_release2() -
						// persona1Summary.get(j).getRelease1_release2()));
						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
						teclasIgualesPersona1.add(persona1Summary.get(j));
					}
					if ((personaPruebaSummary.get(i).getPress1_release2() > persona1Summary.get(j)
							.getPress1_release2())
							&& (personaPruebaSummary.get(i).getPress1_release2()
									- persona1Summary.get(j).getPress1_release2() <= P1R2)
							|| (personaPruebaSummary.get(i).getPress1_release2() < persona1Summary.get(j)
									.getPress1_release2())
									&& (persona1Summary.get(j).getPress1_release2()
											- personaPruebaSummary.get(i).getPress1_release2() <= P1R2)) {
						matchcount++;
						// System.out.println("Found match where i .. " + i +" j .. " + j + " " + "
						// KEYID " + persona1Summary.get(j).getId() + " " + l1 + " " + l2 + "
						// Press1_release2 " + (personaPruebaSummary.get(i).getPress1_release2() -
						// persona1Summary.get(j).getPress1_release2()));
						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
						teclasIgualesPersona1.add(persona1Summary.get(j));
					}
				}
			}
		}
		/*
		 * if (commonKeysPersona1.size() > 26) {
		 * System.out.println("Printing the results of the second classification.");
		 * double poss_by_class2 = classify(commonKeysPersona1, commonKeysPrueba); }
		 */
		double nr = 0;
		double score = 0;
		ResultTable.setMatchesResult(matchcount);
		double[] normality_results = new double[digraphsList.size()];
		if (teclasIgualespersonaPrueba.size() > 3) {
			for (int b = 0; b < digraphsList.size(); b++) {
				IndividialDigraphsSet tempIDS = collectionPD.get(digraphsList.get(b));
				normality_results[b] = probabilityanalysis(tempIDS);
			}
			ResultTable = correlationtest(ResultTable, teclasIgualespersonaPrueba, teclasIgualesPersona1, Name);
			ResultTable = tTest(ResultTable, teclasIgualespersonaPrueba, teclasIgualesPersona1, Name);
			nr = Mean(normality_results);
			result = result + "   " +  "The possiblity of matching this user through pdf is  " + nr;
			result = result + "   " +  "Matchcount = " + matchcount + " Total Sample = " + N_sample * 4;
			score = poss_by_class + nr + ResultTable.getCorrelationTestResult().getA()
					+ ResultTable.getCorrelationTestResult().getB() + +ResultTable.getCorrelationTestResult().getC()
					+ ResultTable.getCorrelationTestResult().getD() + ResultTable.getCorrelationTestResult().getE();

		}
		T_Test tt = ResultTable.gettTestResult();
		int reject= 0;
		if(tt.getA()<0.15) {
			reject ++;
		}
		if(tt.getB()<0.15) {
			reject ++;
		}
		if(tt.getC()<0.15) {
			reject ++;
		}
		if(tt.getD()<0.15) {
			reject ++;
		}
		if(tt.getE()<0.15) {
			reject ++;
		}
		double ttestaverage = (tt.getA() + tt.getB()+ tt.getC()+tt.getD()+tt.getE())/5;
		if(reject >= 2) {
			score = score / 7;
		} else score = score / 6;
		
		ResultTable.setScore(score);		
		result = result + "   " + "The final score is " + score*100;
		ResultTable.setResultptints(result);
		System.out.println(result);
		return ResultTable;
	}

	@Override
	public double probabilityanalysis(IndividialDigraphsSet ids) {
		double x = 0;
		Vector<Key> teclaList1 = ids.getPrueba(); // that particular digraph of person prueba
		Vector<Key> teclaList2 = ids.getPerson(); // that particular digraph of the real person or expected user

		double[] sample1A = new double[teclaList1.size()];
		double[] sample1B = new double[teclaList1.size()];
		double[] sample1C = new double[teclaList1.size()];
		double[] sample1D = new double[teclaList1.size()];
		double[] sample1E = new double[teclaList1.size()];
		for (int i = 0; i < teclaList1.size(); i++) {
			sample1A[i] = teclaList1.get(i).getPress1_release1();
			sample1B[i] = teclaList1.get(i).getPress1_press2();
			sample1C[i] = teclaList1.get(i).getRelease1_press2();
			sample1D[i] = teclaList1.get(i).getRelease1_release2();
			sample1E[i] = teclaList1.get(i).getPress1_release2();
		}

		double[] sample2A = new double[teclaList2.size()];
		double[] sample2B = new double[teclaList2.size()];
		double[] sample2C = new double[teclaList2.size()];
		double[] sample2D = new double[teclaList2.size()];
		double[] sample2E = new double[teclaList2.size()];
		for (int i = 0; i < teclaList2.size(); i++) {
			sample2A[i] = teclaList2.get(i).getPress1_release1();
			sample2B[i] = teclaList2.get(i).getPress1_press2();
			sample2C[i] = teclaList2.get(i).getRelease1_press2();
			sample2D[i] = teclaList2.get(i).getRelease1_release2();
			sample2E[i] = teclaList2.get(i).getPress1_release2();
		}

		x = processProbability(sample1A, sample2A);
		x = x + processProbability(sample1B, sample2B);
		x = x + processProbability(sample1C, sample2C);
		x = x + processProbability(sample1D, sample2D);
		x = x + processProbability(sample1E, sample2E);

		x = x / 5;

		return x;
	}

	public double processProbability(double[] sample1, double[] sample2) {
		double x = 0;
		double mean2 = Mean(sample2);
		double sd2 = standardDeV(sample2);
		double mean1 = Mean(sample1);
		double sd1 = standardDeV(sample1);
		double lb = mean1 - sd1;
		double ub = mean1 + sd1;
//		   System.out.println("");
//		   System.out.println("");
//		   System.out.println("");
//		   System.out.println("**********************************************************************************************************************************");
//		   System.out.println("Mean1 is " + mean1 +" and SD1 is "+ sd1);
//		   System.out.println("Lower Bound is " + lb + "     Upper Bound is" + ub);
//		   System.out.println("Mean2 is " + mean2 +" and SD2 is "+sd2);
//		   printVector(teclaList1, ids.getDigraph());
//		   System.out.println("................................................................................................................................");
//		   printVector(teclaList2, ids.getDigraph());
		if (lb == ub) {
			x = FindProbability(sample2, lb);
		} else if (sd2 > 0) {
			x = checkNormalDistribution(sample2, lb, ub);
		} else if (mean2 - sd2 == mean2) {
			x = FindProbability(sample1, lb);
		} else if (sd1 > 0) {
			x = checkNormalDistribution(sample1, mean2 - sd2, mean2 + sd2);
		}

		return x;
	}

	@Override
	public double checkNormalDistribution(double[] sample, double lowerbound, double upperbound) {
		double aa = 0;
		double mean = Mean(sample);
		double sd = standardDeV(sample);
		NormalDistribution normaldist = new NormalDistribution(mean, sd);

		DecimalFormat df = new DecimalFormat("#.00000");
		if (lowerbound == upperbound) {
			aa = normaldist.cumulativeProbability(lowerbound);
		} else {
			aa = normaldist.cumulativeProbability(lowerbound, upperbound);
		}
		// = Double.parseDouble(df.format(normaldist.sample()));
		aa = Double.parseDouble(df.format(aa));
		// whichDistFits(sample);
		return aa;
	}

	@Override
	public double standardDeV(double[] sample) {
		double a = 0;
		StandardDeviation sd = new StandardDeviation();
		a = sd.evaluate(sample);
		return a;
	}

	@Override
	public double Mean(double[] sample) {
		double a = 0;
		Mean mean = new Mean();
		a = mean.evaluate(sample);
		return a;
	}

	@Override
	public Result correlationtest(Result ResultTable, Vector<Key> teclaList1, Vector<Key> teclaList2, String Name) {
		double[] sample1A = new double[teclaList1.size()];
		double[] sample1B = new double[teclaList1.size()];
		double[] sample1C = new double[teclaList1.size()];
		double[] sample1D = new double[teclaList1.size()];
		double[] sample1E = new double[teclaList1.size()];
		for (int i = 0; i < teclaList1.size(); i++) {
			sample1A[i] = teclaList1.get(i).getPress1_release1();
			sample1B[i] = teclaList1.get(i).getPress1_press2();
			sample1C[i] = teclaList1.get(i).getRelease1_press2();
			sample1D[i] = teclaList1.get(i).getRelease1_release2();
			sample1E[i] = teclaList1.get(i).getPress1_release2();
		}

		double[] sample2A = new double[teclaList2.size()];
		double[] sample2B = new double[teclaList2.size()];
		double[] sample2C = new double[teclaList2.size()];
		double[] sample2D = new double[teclaList2.size()];
		double[] sample2E = new double[teclaList2.size()];
		for (int i = 0; i < teclaList2.size(); i++) {
			sample2A[i] = teclaList2.get(i).getPress1_release1();
			sample2B[i] = teclaList2.get(i).getPress1_press2();
			sample2C[i] = teclaList2.get(i).getRelease1_press2();
			sample2D[i] = teclaList2.get(i).getRelease1_release2();
			sample2E[i] = teclaList2.get(i).getPress1_release2();
		}
		Corelation correlationTestResult = new Corelation();
		double a;
		double b;
		double c;
		double d;
		double e;
		if (teclaList1.size() > 2) {
			a = new PearsonsCorrelation().correlation(sample1A, sample2A);
			b = new PearsonsCorrelation().correlation(sample1B, sample2B);
			c = new PearsonsCorrelation().correlation(sample1C, sample2C);
			d = new PearsonsCorrelation().correlation(sample1D, sample2D);
			e = new PearsonsCorrelation().correlation(sample1E, sample2E);
			correlationTestResult.setA(a);
			correlationTestResult.setB(b);
			correlationTestResult.setC(c);
			correlationTestResult.setD(d);
			correlationTestResult.setE(e);
			result = result + "   " + "Correlation Coeffecient Results with " + Name + " is  P1R1:  " + Double.toString(a)
					+ " P1P2 " + Double.toString(b) + ",  R1P2:  " + Double.toString(c) + ", R1R2:  "
					+ Double.toString(d) + ", P1R2:  " + Double.toString(e);
			ResultTable.setCorrelationTestResult(correlationTestResult);
		}
		return ResultTable;
	}

	@Override
	public double FindProbability(double[] sample, double x) {
		double probability = 0;
		double mean = Mean(sample);
		double sd = standardDeV(sample);
		double a = 0;
		if (mean > x) {
			a = mean - x;
		} else {
			a = x - mean;
		}

		probability = (sd - a) / sd;
		if (probability < 0) {
			probability = 0;
		}
		// System.out.println("Tested Probability: Mean: " + mean + " SD: "+ sd + "
		// Value of A: " + a + " Probability: " + probability);
		return probability;
	}

	@Override
	public int checkIfCopyPasted(Vector<Key> listofKeysRecieved) {
		int a = 0;
//		for (int i = 0; i < listofKeysRecieved.size(); i++) {
//			if(!listofKeysRecieved.get(i).getLetter1().equals(null)) {
//				if(listofKeysRecieved.get(i).getLetter1().equals("ctrlv")) {
//				//if(listofKeysRecieved.get(i).getLetter2().equals("v") || listofKeysRecieved.get(i).getLetter2().equals("V")) {
//					a = 1;
//				//}
//				}
//			}
//		}
		return a;
	}

	public double classify(Vector<Key> traindata, Vector<Key> testdata) throws Exception {
		saveTrainARFF(traindata, "Train_" + name);
		saveTestARFF(testdata, "Test_" + name);
		DataSource source1 = new DataSource("data_workshop/" + "Train_" + name + ".arff");
		Instances traindataset = source1.getDataSet();
		if (traindataset.classIndex() == -1) {
			traindataset.setClassIndex(traindataset.numAttributes() - 1);
		}
		DataSource source2 = new DataSource("data_workshop/" + "Test_" + name + ".arff");
		Instances testdataset = source2.getDataSet();
//        if (testdataset.classIndex() == -1) {
//            System.out.println("reset index...");
//            testdataset.setClassIndex(testdataset.numAttributes() - 1);
//        }

		ArrayList<Attribute> atts = new ArrayList<Attribute>(5);

		atts.add(new Attribute("letter1"));
		atts.add(new Attribute("letter2"));
		atts.add(new Attribute("P1R1"));
		atts.add(new Attribute("P1P2"));
		atts.add(new Attribute("R1P2"));
		atts.add(new Attribute("R1R2"));
		atts.add(new Attribute("P1R2"));
		atts.add(new Attribute(name));

//        Remove removeFilter = new Remove();
//        removeFilter.setAttributeIndices("1,2,3,4,5,6");;
//        removeFilter.setInvertSelection(true);
//        removeFilter.setInputFormat(testdataset);
//        testdataset = Filter.useFilter(testdataset, removeFilter);
		Normalize filterNormtest = new Normalize();
		filterNormtest.setInputFormat(testdataset);
		testdataset = Filter.useFilter(testdataset, filterNormtest);
//        Add filter = new Add();
//        filter.setAttributeIndex("last");
//        filter.setAttributeName(name);
//        filter.setInputFormat(testdataset);
//        testdataset = Filter.useFilter(testdataset, filter);

		double percent = 0.50;
		int trainingSize = (int) Math.round(traindataset.numInstances() * percent);
		int learningSize = traindataset.numInstances() - trainingSize;
		Instances training = new Instances(traindataset, 0, trainingSize);
		if (training.classIndex() == -1) {
			training.setClassIndex(training.numAttributes() - 1);
		}
		Instances learning = new Instances(traindataset, trainingSize, learningSize);
		if (learning.classIndex() == -1) {
			learning.setClassIndex(learning.numAttributes() - 1);
		}
		Normalize filterNormtrain = new Normalize();
		filterNormtrain.setInputFormat(training);
		training = Filter.useFilter(training, filterNormtrain);
		Normalize filterNormlearning = new Normalize();
		filterNormlearning.setInputFormat(learning);
		learning = Filter.useFilter(learning, filterNormlearning);
		/*
		 * LibSVM classifier = new LibSVM(); svm.setSVMType(new
		 * SelectedTag(LibSVM.SVMTYPE_ONE_CLASS_SVM, LibSVM.TAGS_SVMTYPE));
		 * svm.buildClassifier(training);
		 */
		OneClassClassifier one_class_classifier = new OneClassClassifier();
		one_class_classifier.setTargetClassLabel(name);
		Evaluation eval = new Evaluation(training);
		eval.crossValidateModel(one_class_classifier, learning, 12, new Random(1));
		// eval.evaluateModel(classifier, learning);
		// print the results of modeling
		String strSummary = eval.toSummaryString();
		result = result  + strSummary;
		one_class_classifier.buildClassifier(training);
		saveModelToFile(name, one_class_classifier);

//					NaiveBayes nb = new NaiveBayes();
//					nb.buildClassifier(training);
//					Evaluation eval2 = new Evaluation(training);
//					eval2.crossValidateModel(nb, learning, 12, new Random(1));
//					//print the results of modeling
//					String strSummary2 = eval2.toSummaryString();
//					System.out.println("" + strSummary2);
		//vs.visualize(eval);
		double Possibility = execute_classifier(one_class_classifier, testdataset);
		//double Possibility2 = execute_classifier(nb, testdataset);
		//System.out.println("OneClassClassifier is " + Possibility +  " & Naive Bayes is " + Possibility2);
		//Possibility = ((Possibility + Possibility2)/2);
		return Possibility;
	}

	public double execute_classifier(Classifier classifier, Instances testdataset) {
		List<Double> classes = new ArrayList<Double>();
		if (testdataset.classIndex() == -1) {
			testdataset.setClassIndex(testdataset.numAttributes() - 1);
		}
//			Normalize filterNormdataRaw = new Normalize();
//	        filterNormdataRaw.setInputFormat(dataRaw);
//	        dataRaw = Filter.useFilter(dataRaw, filterNormdataRaw);
		for (int i = 0; i < testdataset.size() - 1; i++) {
			Instance instance = testdataset.get(i);
			instance.setClassMissing();
			try {
				classes.add(classifier.classifyInstance(instance));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int yes = 0;
		int no = 0;
		for (int i = 0; i < classes.size(); i++) {
			if (classes.get(i) == 0) {
				yes++;
			} else
				no++;
		}
		double Possibility = ((double) yes) / ((double) classes.size());
		result = result + " " +(
				"Possiblity by classification is " + yes + "/" + classes.size() + "   = " + Possibility * 100 + "% ");

		return Possibility;
	}

	public void saveTestARFF(Vector<Key> vD, String filename) {

		try {
			File file = new File("data_workshop/" + filename + ".arff");
			PrintWriter fw = new PrintWriter(file);
			fw.flush();
			fw.println("@RELATION keys");
			fw.println("@ATTRIBUTE letter1  NUMERIC");
			fw.println("@ATTRIBUTE letter2  NUMERIC");
			fw.println("@ATTRIBUTE P1R1  NUMERIC");
			fw.println("@ATTRIBUTE P1P2  NUMERIC");
			fw.println("@ATTRIBUTE R1P2  NUMERIC");
			fw.println("@ATTRIBUTE R1R2  NUMERIC");
			fw.println("@ATTRIBUTE P1R2  NUMERIC");
			fw.println();
			fw.println("@ATTRIBUTE class {'" + name + "', 'outlier'}");

			fw.println();
			fw.println("@DATA");

			for (int i = 0; i < vD.size(); i++) {
				Key a = vD.get(i);
				fw.println(a.getLetter1() + "," + a.getLetter2() + "," + a.getPress1_press2() + ","
						+ a.getPress1_release1() + "," + a.getRelease1_press2() + "," + a.getRelease1_release2() + "," + a.getPress1_release2()
						+ ", ?");
			}
			fw.close();
		} catch (Exception ex) {
		} finally {
		}
	}

	public void saveTrainARFF(Vector<Key> input, String filename) {
		try {
			String dir = "/" + "data_workshop" + "/";
			;
			// String realPathtoUploads = "C:\\uploadsE65\\" + uploadsDir;
			java.io.File directory = new java.io.File(dir);
			if (!directory.exists()) {
				directory.mkdir();
				// If you require it to make the entire directory path including parents,
				// use directory.mkdirs(); here instead.
			}
			File file = new File("data_workshop/" + filename + ".arff");
			PrintWriter fw = new PrintWriter(file);
			fw.flush();
			fw.println("@RELATION keys");
			fw.println("@ATTRIBUTE letter1  NUMERIC");
			fw.println("@ATTRIBUTE letter2  NUMERIC");
			fw.println("@ATTRIBUTE P1R1  NUMERIC");
			fw.println("@ATTRIBUTE P1P2  NUMERIC");
			fw.println("@ATTRIBUTE R1P2  NUMERIC");
			fw.println("@ATTRIBUTE R1R2  NUMERIC");
			fw.println("@ATTRIBUTE P1R2  NUMERIC");
			fw.println();
			fw.println("@ATTRIBUTE class {'" + name + "', 'outlier'}");
			fw.println();
			fw.println("@DATA");

			List<Long> sampleA = new ArrayList<Long>();
			List<Long> sampleB = new ArrayList<Long>();
			List<Long> sampleC = new ArrayList<Long>();
			List<Long> sampleD = new ArrayList<Long>();
			List<Long> sampleE = new ArrayList<Long>();
			for (int i = 0; i < input.size(); i++) {
				sampleA.add(input.get(i).getPress1_release1());
				sampleB.add(input.get(i).getPress1_press2());
				sampleC.add(input.get(i).getRelease1_press2());
				sampleD.add(input.get(i).getRelease1_release2());
				sampleE.add(input.get(i).getPress1_release2());
			}

			List<Long> sample1A = new ArrayList<Long>();
			List<Long> sample1B = new ArrayList<Long>();
			List<Long> sample1C = new ArrayList<Long>();
			List<Long> sample1D = new ArrayList<Long>();
			List<Long> sample1E = new ArrayList<Long>();
			List<Long> sample2A = new ArrayList<Long>();
			List<Long> sample2B = new ArrayList<Long>();
			List<Long> sample2C = new ArrayList<Long>();
			List<Long> sample2D = new ArrayList<Long>();
			List<Long> sample2E = new ArrayList<Long>();
			Collections.sort(sampleA);
			Collections.sort(sampleB);
			Collections.sort(sampleC);
			Collections.sort(sampleD);
			Collections.sort(sampleE);
			if (input.size() % 2 == 0) {
				sample1A = sampleA.subList(0, sampleA.size() / 2);
				sample2A = sampleA.subList(sampleA.size() / 2, sampleA.size());
				sample1B = sampleB.subList(0, sampleB.size() / 2);
				sample2B = sampleB.subList(sampleB.size() / 2, sampleB.size());
				sample1C = sampleC.subList(0, sampleC.size() / 2);
				sample2C = sampleC.subList(sampleC.size() / 2, sampleC.size());
				sample1D = sampleD.subList(0, sampleD.size() / 2);
				sample2D = sampleD.subList(sampleD.size() / 2, sampleD.size());
				sample1E = sampleE.subList(0, sampleE.size() / 2);
				sample2E = sampleE.subList(sampleE.size() / 2, sampleE.size());
			} else {
				sample1A = sampleA.subList(0, sampleA.size() / 2);
				sample2A = sampleA.subList(sampleA.size() / 2 + 1, sampleA.size());
				sample1B = sampleB.subList(0, sampleB.size() / 2);
				sample2B = sampleB.subList(sampleB.size() / 2 + 1, sampleB.size());
				sample1C = sampleC.subList(0, sampleC.size() / 2);
				sample2C = sampleC.subList(sampleC.size() / 2 + 1, sampleC.size());
				sample1D = sampleD.subList(0, sampleD.size() / 2);
				sample2D = sampleD.subList(sampleD.size() / 2 + 1, sampleD.size());
				sample1E = sampleE.subList(0, sampleE.size() / 2);
				sample2E = sampleE.subList(sampleE.size() / 2 + 1, sampleE.size());
			}
			double Aq1 = getMedian(sample1A);
			double Aq3 = getMedian(sample2A);
			double Bq1 = getMedian(sample1B);
			double Bq3 = getMedian(sample2B);
			double Cq1 = getMedian(sample1C);
			double Cq3 = getMedian(sample2C);
			double Dq1 = getMedian(sample1D);
			double Dq3 = getMedian(sample2D);
			double Eq1 = getMedian(sample1E);
			double Eq3 = getMedian(sample2E);
			double Aiqr = Aq3 - Aq1;
			double Biqr = Bq3 - Bq1;
			double Ciqr = Cq3 - Cq1;
			double Diqr = Dq3 - Dq1;
			double Eiqr = Eq3 - Eq1;
			double AlowerFence = Aq1 - 1.5 * Aiqr;
			double AupperFence = Aq3 + 1.5 * Aiqr;
			double BlowerFence = Bq1 - 1.5 * Biqr;
			double BupperFence = Bq3 + 1.5 * Biqr;
			double ClowerFence = Cq1 - 1.5 * Ciqr;
			double CupperFence = Cq3 + 1.5 * Ciqr;
			double DlowerFence = Dq1 - 1.5 * Diqr;
			double DupperFence = Dq3 + 1.5 * Diqr;
			double ElowerFence = Eq1 - 1.5 * Eiqr;
			double EupperFence = Eq3 + 1.5 * Eiqr;
			for (int i = 0; i < input.size(); i++) {
				Boolean detected = false;
				Key a = input.get(i);
				Long A = a.getPress1_release1();
				Long B = a.getPress1_press2();
				Long C = a.getRelease1_press2();
				Long D = a.getRelease1_release2();
				Long E = a.getPress1_release2();
				double aaa=1000; //1000
				double bbb = 1000;
				double ccc=1000; ////2000
				double ddd = 1000;
				double eee = 2000;
				if (A < AlowerFence+aaa || A > AupperFence-aaa) detected = true;
			
				else if (B < BlowerFence + bbb || B > BupperFence - bbb) detected = true;

				else if (C < ClowerFence + ccc || C > CupperFence - ccc) detected = true; 
				
				else if (D < DlowerFence + ddd || D > DupperFence - ddd) detected = true;
				
				else if (E < ElowerFence + eee || E > EupperFence - eee) detected = true;

				if (detected)
					fw.println(a.getLetter1() + "," + a.getLetter2() + "," + a.getPress1_press2() + ","
							+ a.getPress1_release1() + "," + a.getRelease1_press2() + "," + a.getRelease1_release2() + "," + a.getPress1_release2()
							+ "," + "outlier");
				else
					fw.println(a.getLetter1() + "," + a.getLetter2() + "," + a.getPress1_press2() + ","
							+ a.getPress1_release1() + "," + a.getRelease1_press2() + "," + a.getRelease1_release2() + "," + a.getPress1_release2()
							+ "," + name);
			}

			fw.close();
		} catch (Exception ex) {
		} finally {

		}

	}

	public void saveModelToFile(String filename, Classifier classifier) {
		String dir = "data_workshop/" ;
		try {
			weka.core.SerializationHelper.write(dir + filename + "_model.mdl", classifier);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Classifier loadModelFromFile(String filename) {
		String dir = "data_workshop/" ;
		Classifier model=null;
		try {
			model = (Classifier)weka.core.SerializationHelper.read(dir + filename + "_model.mdl");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	public static List<Long> getOutliers(Vector<Key> input) {
		List<Long> output = new ArrayList<Long>();

		List<Long> sampleA = new ArrayList<Long>();
		List<Long> sampleB = new ArrayList<Long>();
		List<Long> sampleC = new ArrayList<Long>();
		List<Long> sampleD = new ArrayList<Long>();
		List<Long> sampleE = new ArrayList<Long>();
		for (int i = 0; i < input.size(); i++) {
			sampleA.add(input.get(i).getPress1_release1());
			sampleB.add(input.get(i).getPress1_press2());
			sampleC.add(input.get(i).getRelease1_press2());
			sampleD.add(input.get(i).getRelease1_release2());
			sampleE.add(input.get(i).getPress1_release2());
		}

		List<Long> sample1A = new ArrayList<Long>();
		List<Long> sample1B = new ArrayList<Long>();
		List<Long> sample1C = new ArrayList<Long>();
		List<Long> sample1D = new ArrayList<Long>();
		List<Long> sample1E = new ArrayList<Long>();
		List<Long> sample2A = new ArrayList<Long>();
		List<Long> sample2B = new ArrayList<Long>();
		List<Long> sample2C = new ArrayList<Long>();
		List<Long> sample2D = new ArrayList<Long>();
		List<Long> sample2E = new ArrayList<Long>();
		Collections.sort(sampleA);
		Collections.sort(sampleB);
		Collections.sort(sampleC);
		Collections.sort(sampleD);
		Collections.sort(sampleE);
		if (input.size() % 2 == 0) {
			sample1A = sampleA.subList(0, sampleA.size() / 2);
			sample2A = sampleA.subList(sampleA.size() / 2, sampleA.size());
			sample1B = sampleB.subList(0, sampleB.size() / 2);
			sample2B = sampleB.subList(sampleB.size() / 2, sampleB.size());
			sample1C = sampleC.subList(0, sampleC.size() / 2);
			sample2C = sampleC.subList(sampleC.size() / 2, sampleC.size());
			sample1D = sampleD.subList(0, sampleD.size() / 2);
			sample2D = sampleD.subList(sampleD.size() / 2, sampleD.size());
			sample1E = sampleE.subList(0, sampleE.size() / 2);
			sample2E = sampleE.subList(sampleE.size() / 2, sampleE.size());
		} else {
			sample1A = sampleA.subList(0, sampleA.size() / 2);
			sample2A = sampleA.subList(sampleA.size() / 2 + 1, sampleA.size());
			sample1B = sampleB.subList(0, sampleB.size() / 2);
			sample2B = sampleB.subList(sampleB.size() / 2 + 1, sampleB.size());
			sample1C = sampleC.subList(0, sampleC.size() / 2);
			sample2C = sampleC.subList(sampleC.size() / 2 + 1, sampleC.size());
			sample1D = sampleD.subList(0, sampleD.size() / 2);
			sample2D = sampleD.subList(sampleD.size() / 2 + 1, sampleD.size());
			sample1E = sampleE.subList(0, sampleE.size() / 2);
			sample2E = sampleE.subList(sampleE.size() / 2 + 1, sampleE.size());
		}
		double Aq1 = getMedian(sample1A);
		double Aq3 = getMedian(sample2A);
		double Bq1 = getMedian(sample1B);
		double Bq3 = getMedian(sample2B);
		double Cq1 = getMedian(sample1C);
		double Cq3 = getMedian(sample2C);
		double Dq1 = getMedian(sample1D);
		double Dq3 = getMedian(sample2D);
		double Eq1 = getMedian(sample1E);
		double Eq3 = getMedian(sample2E);
		double Aiqr = Aq3 - Aq1;
		double Biqr = Bq3 - Bq1;
		double Ciqr = Cq3 - Cq1;
		double Diqr = Dq3 - Dq1;
		double Eiqr = Eq3 - Eq1;
		double AlowerFence = Aq1 - 1.5 * Aiqr;
		double AupperFence = Aq3 + 1.5 * Aiqr;
		double BlowerFence = Bq1 - 1.5 * Biqr;
		double BupperFence = Bq3 + 1.5 * Biqr;
		double ClowerFence = Cq1 - 1.5 * Ciqr;
		double CupperFence = Cq3 + 1.5 * Ciqr;
		double DlowerFence = Dq1 - 1.5 * Diqr;
		double DupperFence = Dq3 + 1.5 * Diqr;
		double ElowerFence = Eq1 - 1.5 * Eiqr;
		double EupperFence = Eq3 + 1.5 * Eiqr;
		for (int i = 0; i < input.size(); i++) {
			Boolean detected = false;
			Key a = input.get(i);
			Long A = a.getPress1_release1();
			Long B = a.getPress1_press2();
			Long C = a.getRelease1_press2();
			Long D = a.getRelease1_release2();
			Long E = a.getPress1_release2();
			if (A < AlowerFence || A > AupperFence)
				output.add(input.get(i).getPress1_release1());

			
			if (B < BlowerFence || B > BupperFence)
			output.add(input.get(i).getPress1_press2());
			

			if (C < ClowerFence || C > CupperFence)
				output.add(input.get(i).getRelease1_press2());

			
			if (D < DlowerFence || D > DupperFence)
			output.add(input.get(i).getRelease1_release2());
			
			if (E < ElowerFence || E > EupperFence)
				output.add(input.get(i).getPress1_release2());
			
			
		}
		return output;
	}

	private static double getMedian(List<Long> data) {
		if (data.size() % 2 == 0)
			return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
		else
			return data.get(data.size() / 2);
	}
}
