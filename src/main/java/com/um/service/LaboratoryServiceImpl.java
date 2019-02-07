package com.um.service;
import java.sql.SQLException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.um.model.CollectionOfPairedDigraphs;
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

@Service("laboratoryService")
public class LaboratoryServiceImpl implements LaboratoryService {
	@Autowired
	private KmService kmService;
	@Autowired
	 UsuarioService us;
	private Vector <Object> keys4mdbEvry1 = new Vector  <Object> ();
    private static long P1R1  = 500;
    private static long P1P2  = 100;
    private static long R1P2  = 100;
    private static long R1R2  = 100;   
	@Override
	public Vector <Object> bringAllData(HashMap<String, Result> ResultTable) throws SQLException {
		Vector <Object> AllData = new Vector <Object>();
		Vector <Usuario> personas = kmService.bringlistofPersons();
		Vector<String> userNames =  new Vector<String>();
		for (int i = 0; i < personas.size(); i++) {
			Vector <Key> registrosTemp = kmService.retrieveKeysFromDB(personas.get(i).getMatricula());
			for(int jj=0; jj<registrosTemp.size(); jj++) {
				if(registrosTemp.get(jj).getRelease1_press2() >= 50000) {
					registrosTemp.remove(jj);
				}
			}
			keys4mdbEvry1.add(registrosTemp);
			printVector(registrosTemp,personas.get(i).getNombre());
			userNames.add(personas.get(i).getNombre());
		}
		Result res = null;
		for (int i = 0; i < userNames.size(); i++) {
			ResultTable.put(userNames.get(i), res);
		}
		AllData.add(keys4mdbEvry1);
		AllData.add(userNames);
		return AllData ;	//All data is an array of objects, where the first object is the array of list of keycombinations of all the users from the database and the second is
							//the usernames of all the users.
	}
	
	@Override
    public HashMap<String, Result> MapResultsOfUsers(HashMap<String, Result> ResultTable){
		ArrayList <Usuario> userNames = us.listaUsuarios();
		
		Result res = new Result();
		for (int i = 0; i < userNames.size(); i++) {
			ResultTable.put(userNames.get(i).getMatricula(), res);
		}
		return ResultTable;
    }

	
	
	@Override
    public void printVector(Vector<Key> printingList, String name){
    	System.out.println();
    	System.out.println("The vector " + name + " is printing below."); 
    	for(int i=0; i<printingList.size(); i++){
            Key temp = printingList.get(i);
            System.out.println(temp.getLetter1() + ", " +  temp.getLetter2() + ", " + temp.getPress1_release1() + ", " + temp.getPress1_press2() + ", " + temp.getRelease1_press2() + ", " + temp.getRelease1_release2());
        }
    	System.out.println("The vector " + name + " finished printing");
    	System.out.println();
    }
	@Override
    public HashMap<String, Result> tTest(HashMap<String, Result> ResultTable, Vector<Key> teclaList1, Vector<Key> teclaList2, String Name ) {   	
    	double[] sample1A = new double[teclaList1.size()];
    	double[] sample1B = new double[teclaList1.size()];
    	double[] sample1C = new double[teclaList1.size()];
    	double[] sample1D = new double[teclaList1.size()];
    	for (int i = 0; i < teclaList1.size(); i++) {
    		sample1A[i]= teclaList1.get(i).getPress1_release1();
    		sample1B[i] = teclaList1.get(i).getPress1_press2();
    		sample1C[i] = teclaList1.get(i).getRelease1_press2();
    		sample1D[i] = teclaList1.get(i).getRelease1_release2();
		}
    	
    	double[] sample2A = new double[teclaList2.size()];
    	double[] sample2B = new double[teclaList2.size()];
    	double[] sample2C = new double[teclaList2.size()];
    	double[] sample2D = new double[teclaList2.size()];
    	for (int i = 0; i < teclaList2.size(); i++) {
    		sample2A[i]= teclaList2.get(i).getPress1_release1();
    		sample2B[i] = teclaList2.get(i).getPress1_press2();
    		sample2C[i] = teclaList2.get(i).getRelease1_press2();
    		sample2D[i] = teclaList2.get(i).getRelease1_release2();
		}
    	T_Test tTestResult = new T_Test();
		double pValuePress1_release1;
		double pValuePress1_press2;
		double pValueRelease1_press2;
		double pValueRelease1_release2;
		DecimalFormat df = new DecimalFormat("#.00000");
		TTest ttest = new TTest();
		pValuePress1_release1 =Double.parseDouble(df.format( ttest.tTest(sample1A, sample2A)));
		pValuePress1_press2 = Double.parseDouble(df.format(ttest.tTest(sample1B, sample2B)));
		pValueRelease1_press2 =Double.parseDouble(df.format( ttest.tTest(sample1C, sample2C)));
		pValueRelease1_release2 =Double.parseDouble(df.format( ttest.tTest(sample1D, sample2D)));
		tTestResult.setA(pValuePress1_release1);
		tTestResult.setB(pValuePress1_press2);
		tTestResult.setC(pValueRelease1_press2);
		tTestResult.setD(pValueRelease1_release2);
		ResultTable.get(Name).settTestResult(tTestResult);
		System.out.println("T-Test Results : " + "P1R1: " + pValuePress1_release1 + " and " + "P1P2: " + pValuePress1_press2 + " and " + "R1P2: " + pValueRelease1_press2 + " and " + "R1R2: " + pValueRelease1_release2 );
		return ResultTable;
		
    }
	
	@Override
	public HashMap<String, Result> checkMatches(HashMap<String, Result> ResultTable, Vector<Key> persona1Summary, Vector<Key> personaPruebaSummary, String Name ) {
		//HashMap<String, Object> IPD = new HashMap<String, Object> (); //individual or unique_pair_diagraph
		HashMap<String, IndividialDigraphsSet> collectionPD = new HashMap<String, IndividialDigraphsSet> ();  //collection_of_paired_diagraphs_of_both_persons
    	Vector<Key> teclasIgualespersonaPrueba = new Vector <Key> ();
    	Vector<Key> teclasIgualesPersona1 = new Vector <Key> ();
    	for(int i=1; i<personaPruebaSummary.size(); i++) {                //Prueba is i              //person is j
    		int confirmation = 0;
    		String l1 = personaPruebaSummary.get(i).getLetter1();
        	String l2 = personaPruebaSummary.get(i).getLetter2();
    		for(int j=0; j<persona1Summary.size(); j++) {
    		  if(j<persona1Summary.size() && i<personaPruebaSummary.size()){
    			  if(persona1Summary.get(j).getLetter1().equals(l1) && persona1Summary.get(j).getLetter2().equals(l2)) {
    				//no matter the value of any variables, its a set of collections of same recurring digraphs
    				  String digraph = l1+l2;
    				  if(!collectionPD.containsKey(digraph)) {
    					  Vector<Key> tempkeys = new Vector <Key> ();
    					  IndividialDigraphsSet ids = new IndividialDigraphsSet();
    					  tempkeys.add(personaPruebaSummary.get(i));
    					  tempkeys.add(persona1Summary.get(j));
    					  ids.setPrueba(tempkeys);
    					  ids.setPerson(tempkeys);
    					  collectionPD.put(digraph, ids);
    				  }else {
    					  if(confirmation==0) {										//to avoid duplication of keys(model) while sorting out common digraphs.
    						  collectionPD.get(digraph).getPrueba().add(personaPruebaSummary.get(i));
    						  confirmation = 1;
    					  }
    					  collectionPD.get(digraph).getPerson().add(persona1Summary.get(j));    					  
    				  }
    				  
    				  //Matching Analysis and sorting out common key press
    					if((personaPruebaSummary.get(i).getPress1_release1() > persona1Summary.get(j).getPress1_release1()) && (personaPruebaSummary.get(i).getPress1_release1() - persona1Summary.get(j).getPress1_release1() <= P1R1) || (personaPruebaSummary.get(i).getPress1_release1() < persona1Summary.get(j).getPress1_release1()) && (persona1Summary.get(j).getPress1_release1() - personaPruebaSummary.get(i).getPress1_release1() <= P1R1)  ) {
    						ResultTable.get(Name).setMatchesResult(ResultTable.get(Name).getMatchesResult() + 1);
    						System.out.println("Found USER1 " + l1 + "  " + l2 + " Press1_release1 "  + (personaPruebaSummary.get(i).getPress1_release1() - persona1Summary.get(j).getPress1_release1()));
    						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
    						teclasIgualesPersona1.add(persona1Summary.get(j));
    					}
    				
    					if((personaPruebaSummary.get(i).getPress1_press2() > persona1Summary.get(j).getPress1_press2()) && (personaPruebaSummary.get(i).getPress1_press2() - persona1Summary.get(j).getPress1_press2() <= P1P2) || (personaPruebaSummary.get(i).getPress1_press2() < persona1Summary.get(j).getPress1_press2()) && (persona1Summary.get(j).getPress1_press2() - personaPruebaSummary.get(i).getPress1_press2() <= P1P2)  ) {
    						ResultTable.get(Name).setMatchesResult(ResultTable.get(Name).getMatchesResult() + 1);
    						System.out.println("Found USER1 " + l1 + "  " + l2 + " Press1_press2 "  + (personaPruebaSummary.get(i).getPress1_press2() - persona1Summary.get(j).getPress1_press2()));
    						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
    						teclasIgualesPersona1.add(persona1Summary.get(j));
    					}
    					
    					if((personaPruebaSummary.get(i).getRelease1_press2() > persona1Summary.get(j).getRelease1_press2()) && (personaPruebaSummary.get(i).getRelease1_press2() - persona1Summary.get(j).getRelease1_press2() <= R1P2) || (personaPruebaSummary.get(i).getRelease1_press2() < persona1Summary.get(j).getRelease1_press2()) && (persona1Summary.get(j).getRelease1_press2() - personaPruebaSummary.get(i).getRelease1_press2() <= R1P2)  ) {
    						ResultTable.get(Name).setMatchesResult(ResultTable.get(Name).getMatchesResult() + 1);
    						System.out.println("Found USER1 " + l1 + "  " + l2 + " Release1_press2 "  + (personaPruebaSummary.get(i).getRelease1_press2() - persona1Summary.get(j).getRelease1_press2()));
    						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
    						teclasIgualesPersona1.add(persona1Summary.get(j));
    					}
    					
    					if((personaPruebaSummary.get(i).getRelease1_release2() > persona1Summary.get(j).getRelease1_release2()) && (personaPruebaSummary.get(i).getRelease1_release2() - persona1Summary.get(j).getRelease1_release2() <= R1R2) || (personaPruebaSummary.get(i).getRelease1_release2() < persona1Summary.get(j).getRelease1_release2()) && (persona1Summary.get(j).getRelease1_release2() - personaPruebaSummary.get(i).getRelease1_release2() <= R1R2)  ) {
    						ResultTable.get(Name).setMatchesResult(ResultTable.get(Name).getMatchesResult() + 1);
    						System.out.println("Found USER1 " + l1 + "  " + l2 + " Release1_release2 "  + (personaPruebaSummary.get(i).getRelease1_release2() - persona1Summary.get(j).getRelease1_release2()));
    						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
    						teclasIgualesPersona1.add(persona1Summary.get(j));
    					}    					
    			}
    		  }
    		}
         }
    	
    	
    	
    	
    	if(teclasIgualespersonaPrueba.size() > 3) {
    		ResultTable = correlationtest(ResultTable, teclasIgualespersonaPrueba, teclasIgualesPersona1, Name);
        	ResultTable = tTest(ResultTable, teclasIgualespersonaPrueba, teclasIgualesPersona1, Name);
        	distributionanalysis(collectionPD);
    	}    	
    	return ResultTable;
    }
	
	@Override
	public HashMap<String, Result> correlationtest(HashMap<String, Result> ResultTable, Vector<Key> teclaList1, Vector<Key> teclaList2, String Name ) {
    	double[] sample1A = new double[teclaList1.size()];
    	double[] sample1B = new double[teclaList1.size()];
    	double[] sample1C = new double[teclaList1.size()];
    	double[] sample1D = new double[teclaList1.size()];
    	for (int i = 0; i < teclaList1.size(); i++) {
    		sample1A[i]= teclaList1.get(i).getPress1_release1();
    		sample1B[i] = teclaList1.get(i).getPress1_press2();
    		sample1C[i] = teclaList1.get(i).getRelease1_press2();
    		sample1D[i] = teclaList1.get(i).getRelease1_release2();
		}
    	
    	double[] sample2A = new double[teclaList2.size()];
    	double[] sample2B = new double[teclaList2.size()];
    	double[] sample2C = new double[teclaList2.size()];
    	double[] sample2D = new double[teclaList2.size()];
    	for (int i = 0; i < teclaList2.size(); i++) {
    		sample2A[i]= teclaList2.get(i).getPress1_release1();
    		sample2B[i] = teclaList2.get(i).getPress1_press2();
    		sample2C[i] = teclaList2.get(i).getRelease1_press2();
    		sample2D[i] = teclaList2.get(i).getRelease1_release2();
		}
    	
    	Corelation correlationTestResult = new Corelation();
		double a;
		double b;
		double c;
		double d;
    	if(teclaList1.size()>2) {
	    	a = new PearsonsCorrelation().correlation(sample1A, sample2A);
	    	b = new PearsonsCorrelation().correlation(sample1B, sample2B);
	    	c = new PearsonsCorrelation().correlation(sample1C, sample2C);
	    	d =  new PearsonsCorrelation().correlation(sample1D, sample2D);
	    	correlationTestResult.setA(a);
	    	correlationTestResult.setB(b);
	    	correlationTestResult.setC(c);
	    	correlationTestResult.setD(d);
			System.out.println("Correlation Coeffecient Results with " + Name + " is  P1R1:  " + Double.toString(a) + " P1P2 " + Double.toString(b) + " is  R1P2:  " + Double.toString(c) + " is  R1R2:  " + Double.toString(d) );
			ResultTable.get(Name).setCorrelationTestResult(correlationTestResult);
    	}
    	return ResultTable;
    }
	

	@Override
	public double distributionanalysis( HashMap<String, IndividialDigraphsSet> digraphsCollection) {
		double x=0;
		double[] distResult= new  double [digraphsCollection.size()];
		for(int a = 0; a<digraphsCollection.size(); a++) {
				@SuppressWarnings("unchecked")
				HashMap<String, IndividialDigraphsSet> entry = (HashMap<String, IndividialDigraphsSet>) digraphsCollection.entrySet().iterator().next();
				String digraph = ((Entry<String, IndividialDigraphsSet>) entry).getKey();
				IndividialDigraphsSet ids = ((Entry<String, IndividialDigraphsSet>) entry).getValue();
				Vector<Key> teclaList1 = ids.getPrueba();  //that particular digraph of person prueba
				Vector<Key> teclaList2 = ids.getPerson();				//that particular digraph of the real person or expected user
				double[] sample1A = new double[teclaList1.size()];
		    	double[] sample1B = new double[teclaList1.size()];
		    	double[] sample1C = new double[teclaList1.size()];
		    	double[] sample1D = new double[teclaList1.size()];
		    	for (int i = 0; i < teclaList1.size(); i++) {
		    		sample1A[i]= teclaList1.get(i).getPress1_release1();
		    		sample1B[i] = teclaList1.get(i).getPress1_press2();
		    		sample1C[i] = teclaList1.get(i).getRelease1_press2();
		    		sample1D[i] = teclaList1.get(i).getRelease1_release2();
				}
		    	
		    	double[] sample2A = new double[teclaList2.size()];
		    	double[] sample2B = new double[teclaList2.size()];
		    	double[] sample2C = new double[teclaList2.size()];
		    	double[] sample2D = new double[teclaList2.size()];
		    	for (int i = 0; i < teclaList2.size(); i++) {
		    		sample2A[i]= teclaList2.get(i).getPress1_release1();
		    		sample2B[i] = teclaList2.get(i).getPress1_press2();
		    		sample2C[i] = teclaList2.get(i).getRelease1_press2();
		    		sample2D[i] = teclaList2.get(i).getRelease1_release2();
				}
		   double lb = Mean(sample1A) - standardDeV(sample1A);
		   double ub = Mean(sample1A) + standardDeV(sample1A);
		   distResult[a] = checkNormalDistribution(sample2A,lb,ub);
		   System.out.println("Probability of " + digraph + " is "+ distResult[a]);
		}
		return x;
	}
		
	
	@Override
	public double checkNormalDistribution(double[] sample, double lowerbound, double upperbound) {
		double x=0;
		double aa ;
		
		NormalDistribution normaldist = new NormalDistribution(standardDeV(sample), Mean(sample) );
		
		DecimalFormat df = new DecimalFormat("#.00000");
		aa = normaldist.cumulativeProbability(lowerbound,upperbound);
		//= Double.parseDouble(df.format(normaldist.sample()));
		
		aa= Double.parseDouble(df.format(aa));
		System.out.println("Normal Dist: " + aa);
		return aa;
	}
	
	@Override
	public double standardDeV(double[] sample) {
		double a=0;
		StandardDeviation sd = new StandardDeviation();
		 a = sd.evaluate(sample);
		return a;
	}
	
	@Override
	public double Mean(double[] sample) {
		double a=0;
		Mean mean = new Mean();
		 a = mean.evaluate(sample);
		return a;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public int checkIfCopyPasted(Vector <Key> listofKeysRecieved) {
		int a=0;
		for (int i = 0; i < listofKeysRecieved.size(); i++) {
			if(listofKeysRecieved.get(i).getLetter1().equals("ctrlv")) {
				//if(listofKeysRecieved.get(i).getLetter2().equals("v") || listofKeysRecieved.get(i).getLetter2().equals("V")) {
					a = 1;
				//}
			}
		}
		return a;
	}
	
	
	
	
	
}
