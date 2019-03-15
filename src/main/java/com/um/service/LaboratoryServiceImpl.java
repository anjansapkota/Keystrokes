package com.um.service;
import java.io.PrintWriter;
import java.sql.SQLException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
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

import umontreal.ssj.probdist.*;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.OneClassClassifier;

@Service("laboratoryService")
public class LaboratoryServiceImpl implements LaboratoryService {
	@Autowired
	private KmService kmService;
	@Autowired
	 UsuarioService us;
	private Vector <Object> keys4mdbEvry1 = new Vector  <Object> ();
    private static long P1R1  = 800;
    private static long P1P2  = 800;
    private static long R1P2  = 800;
    private static long R1R2  = 800;
    private String name = "";
	@Override
	public Vector <Object> bringAllData(Result ResultTable) throws SQLException {
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
		AllData.add(keys4mdbEvry1);
		AllData.add(userNames);
		return AllData ;	//All data is an array of objects, where the first object is the array of list of keycombinations of all the users from the database and the second is
							//the usernames of all the users.
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
    public Result tTest(Result ResultTable, Vector<Key> teclaList1, Vector<Key> teclaList2, String Name ) {   	
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
		ResultTable.settTestResult(tTestResult);
		System.out.println("T-Test Results : " + "P1R1: " + pValuePress1_release1 + " and " + "P1P2: " + pValuePress1_press2 + " and " + "R1P2: " + pValueRelease1_press2 + " and " + "R1R2: " + pValueRelease1_release2 );
		return ResultTable;
		
    }
	
	@Override
	public Result checkMatches(Result ResultTable, Vector<Key> persona1Summary, Vector<Key> personaPruebaSummary, String Name ) throws Exception {
		name = Name;
		classify (persona1Summary, personaPruebaSummary);		
		//HashMap<String, Object> IPD = new HashMap<String, Object> (); //individual or unique_pair_diagraph
		int matchcount = 0;
		int N_sample = 0;
		HashMap<Long, IndividialDigraphsSet> collectionPD = new HashMap<Long, IndividialDigraphsSet> ();  //collection_of_paired_diagraphs_of_both_persons
		Vector<Long> digraphsList = new Vector<Long>();
    	Vector<Key> teclasIgualespersonaPrueba = new Vector <Key> ();
    	Vector<Key> teclasIgualesPersona1 = new Vector <Key> ();
    	for(int i=1; i<personaPruebaSummary.size(); i++) {                //Prueba is i              //person is j
    		int confirmation = 0;
    		long l1 = personaPruebaSummary.get(i).getLetter1();
    		long l2 = personaPruebaSummary.get(i).getLetter2();
    		for(int j=0; j<persona1Summary.size(); j++) {
    			  if(persona1Summary.get(j).getLetter1() == l1 && persona1Summary.get(j).getLetter2() == l2) {
    				  N_sample++;
    				//no matter the value of any variables, its a set of collections of same recurring digraphs
    				  Long digraph = l1+l2;
    				  if(!collectionPD.containsKey(digraph)) {
    					  Vector<Key> tempkeys1 = new Vector <Key> ();
    					  Vector<Key> tempkeys2 = new Vector <Key> ();
    					  IndividialDigraphsSet ids = new IndividialDigraphsSet();
    					  tempkeys1.add(personaPruebaSummary.get(i));
    					  tempkeys2.add(persona1Summary.get(j));
    					  digraphsList.add(digraph);
    					  ids.setDigraph(digraph);
    					  ids.setPrueba(tempkeys1);
    					  ids.setPerson(tempkeys2);
    					  collectionPD.put(digraph, ids);
    					  confirmation = 1;
    				  }else if(confirmation==0) {										//to avoid duplication of keys(model) while sorting out common digraphs.
    						  collectionPD.get(digraph).getPrueba().add(personaPruebaSummary.get(i));
    						  confirmation = 1;
    					  }
    					  collectionPD.get(digraph).getPerson().add(persona1Summary.get(j));    					  
    				  
    				  
    				  //Matching Analysis and sorting out common key press
    					if((personaPruebaSummary.get(i).getPress1_release1() > persona1Summary.get(j).getPress1_release1()) && (personaPruebaSummary.get(i).getPress1_release1() - persona1Summary.get(j).getPress1_release1() <= P1R1) || (personaPruebaSummary.get(i).getPress1_release1() < persona1Summary.get(j).getPress1_release1()) && (persona1Summary.get(j).getPress1_release1() - personaPruebaSummary.get(i).getPress1_release1() <= P1R1)  ) {
    						matchcount++;
    						//System.out.println("Found match where i .. " + i +" j .. " +  j + "  " + " KEYID " +  persona1Summary.get(j).getId() + "  " + l1 + "  " + l2 + " Press1_release1 "  + (personaPruebaSummary.get(i).getPress1_release1() - persona1Summary.get(j).getPress1_release1()));
    						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
    						teclasIgualesPersona1.add(persona1Summary.get(j));
    					}
    				
    					if((personaPruebaSummary.get(i).getPress1_press2() > persona1Summary.get(j).getPress1_press2()) && (personaPruebaSummary.get(i).getPress1_press2() - persona1Summary.get(j).getPress1_press2() <= P1P2) || (personaPruebaSummary.get(i).getPress1_press2() < persona1Summary.get(j).getPress1_press2()) && (persona1Summary.get(j).getPress1_press2() - personaPruebaSummary.get(i).getPress1_press2() <= P1P2)  ) {
    						matchcount++;
    						//System.out.println("Found match where i .. " + i +" j .. " +  j + "  " + " KEYID " +  persona1Summary.get(j).getId() + "  " + l1 + "  " + l2 + " Press1_press2 "  + (personaPruebaSummary.get(i).getPress1_press2() - persona1Summary.get(j).getPress1_press2()));
    						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
    						teclasIgualesPersona1.add(persona1Summary.get(j));
    					}
    					
    					if((personaPruebaSummary.get(i).getRelease1_press2() > persona1Summary.get(j).getRelease1_press2()) && (personaPruebaSummary.get(i).getRelease1_press2() - persona1Summary.get(j).getRelease1_press2() <= R1P2) || (personaPruebaSummary.get(i).getRelease1_press2() < persona1Summary.get(j).getRelease1_press2()) && (persona1Summary.get(j).getRelease1_press2() - personaPruebaSummary.get(i).getRelease1_press2() <= R1P2)  ) {
    						matchcount++;
    						//System.out.println("Found match where i .. " + i +" j .. " +  j + "  " + " KEYID " +  persona1Summary.get(j).getId() + "  " + l1 + "  " + l2 + " Release1_press2 "  + (personaPruebaSummary.get(i).getRelease1_press2() - persona1Summary.get(j).getRelease1_press2()));
    						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
    						teclasIgualesPersona1.add(persona1Summary.get(j));
    					}
    					
    					if((personaPruebaSummary.get(i).getRelease1_release2() > persona1Summary.get(j).getRelease1_release2()) && (personaPruebaSummary.get(i).getRelease1_release2() - persona1Summary.get(j).getRelease1_release2() <= R1R2) || (personaPruebaSummary.get(i).getRelease1_release2() < persona1Summary.get(j).getRelease1_release2()) && (persona1Summary.get(j).getRelease1_release2() - personaPruebaSummary.get(i).getRelease1_release2() <= R1R2)  ) {
    						matchcount++;
    						//System.out.println("Found match where i .. " + i +" j .. " +  j + "  " + " KEYID " +  persona1Summary.get(j).getId() + "  " + l1 + "  " + l2 + " Release1_release2 "  + (personaPruebaSummary.get(i).getRelease1_release2() - persona1Summary.get(j).getRelease1_release2()));
    						teclasIgualespersonaPrueba.add(personaPruebaSummary.get(i));
    						teclasIgualesPersona1.add(persona1Summary.get(j));
    					}    					
    			  	}
    			}
    		  
    		}   	
    	double nr = 0;
    	double score = 0;
    	ResultTable.setMatchesResult(matchcount);
    	double[] normality_results = new double[digraphsList.size()];
    	if(teclasIgualespersonaPrueba.size() > 3) {    		
        	for(int b =0; b < digraphsList.size(); b++) {
        		IndividialDigraphsSet tempIDS = collectionPD.get(digraphsList.get(b));
        		normality_results[b] = probabilityanalysis(tempIDS);
        	}
        	ResultTable = correlationtest(ResultTable, teclasIgualespersonaPrueba, teclasIgualesPersona1, Name);
        	ResultTable = tTest(ResultTable, teclasIgualespersonaPrueba, teclasIgualesPersona1, Name);
        	nr = Mean(normality_results);
        	System.out.println("The possiblity of matching this user through pdf is  "+ nr);
        	System.out.println("Matchcount = "+ matchcount  + " Total Sample = "+ N_sample*4);
        	score = nr + ResultTable.getCorrelationTestResult().getA() + ResultTable.getCorrelationTestResult().getB() + + ResultTable.getCorrelationTestResult().getC()+ ResultTable.getCorrelationTestResult().getD();
        	
    	}
    	score = score/5;
    	ResultTable.setScore(score);    	
    	return ResultTable;
    }
	

	@Override
	public double probabilityanalysis( IndividialDigraphsSet ids) {
		double x=0;
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
		    	
		x = processProbability(sample1A, sample2A);
		x = x + processProbability(sample1B, sample2B);
		x = x + processProbability(sample1C, sample2C);
		x = x + processProbability(sample1D, sample2D);
		
		x = x/4;
		
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
		   if(lb == ub) {
			   x = FindProbability(sample2, lb);
		   } else if(sd2 > 0) {
			   x = checkNormalDistribution(sample2,lb,ub);
		   }else if(mean2-sd2 == mean2) {
			   x = FindProbability(sample1, lb);
		   }else if(sd1 > 0) {
			   x = checkNormalDistribution(sample1, mean2-sd2,mean2+sd2);
		   }
		   
		  return x; 
	}
	
	
	@Override
	public double checkNormalDistribution(double[] sample, double lowerbound, double upperbound) {
		double aa = 0 ;
		double mean = Mean(sample);
		double sd = standardDeV(sample);
		NormalDistribution normaldist = new NormalDistribution(mean, sd);
		
		DecimalFormat df = new DecimalFormat("#.00000");
		if(lowerbound == upperbound) {
			aa = normaldist.cumulativeProbability(lowerbound);
		} else {
			aa = normaldist.cumulativeProbability(lowerbound,upperbound);
			}
		//= Double.parseDouble(df.format(normaldist.sample()));
		aa= Double.parseDouble(df.format(aa));
		//whichDistFits(sample);
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
	public Result correlationtest(Result ResultTable, Vector<Key> teclaList1, Vector<Key> teclaList2, String Name ) {
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
			ResultTable.setCorrelationTestResult(correlationTestResult);
    	}
    	return ResultTable;
    }
	
	
	@Override
	public double FindProbability(double[] sample, double x) {
		double probability=0;
		double mean = Mean(sample);
		double sd = standardDeV(sample);
		double a = 0;
		if(mean > x) {
			a = mean -x;
		}
		else {
			a = x - mean;
			}
		
		probability = (sd - a)/sd;
		if(probability<0){
			probability = 0;
		}
		//System.out.println("Tested Probability: Mean: " + mean + " SD: "+ sd + " Value of A: " + a + " Probability: " + probability);
		return probability;
	}
	
	
	@Override
	public int checkIfCopyPasted(Vector <Key> listofKeysRecieved) {
		int a=0;
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
	
	
	public void whichDistFits(double[] data) {
		NormalDist.getInstanceFromMLE(data, data.length);
	}
	
	public void classify (Vector <Key> traindata, Vector <Key> testdata) throws Exception {
		saveTrainARFF(traindata, "Train_" + name);
		saveTestARFF(testdata, "Test_" + name);
		DataSource source1 = new DataSource("Train_" + name+".arff");
        Instances traindataset  = source1.getDataSet();
        if (traindataset.classIndex() == -1) {
            System.out.println("reset index...");
            traindataset.setClassIndex(traindataset.numAttributes() - 1);
        }
        DataSource source2 = new DataSource("Test_" + name+".arff");
        Instances testdataset  = source2.getDataSet();
        if (testdataset.classIndex() == -1) {
            System.out.println("reset index...");
            testdataset.setClassIndex(testdataset.numAttributes() - 1);
        }
        
        double percent = 0.50;
        int trainingSize = (int) Math.round(traindataset.numInstances() * percent);
        int learningSize = traindataset.numInstances() - trainingSize;
        Instances training = new Instances(traindataset, 0, trainingSize);
        if (training.classIndex() == -1) {
            System.out.println("reset index...");
            training.setClassIndex(training.numAttributes() - 1);
        }
        Instances learning = new Instances(traindataset, trainingSize, learningSize);
        if (learning.classIndex() == -1) {
            System.out.println("reset index...");
            learning.setClassIndex(learning.numAttributes() - 1);
        }
        Normalize filterNormtrain = new Normalize();
        filterNormtrain.setInputFormat(training);
        training = Filter.useFilter(training, filterNormtrain);
        Normalize filterNormlearning = new Normalize();
        filterNormlearning.setInputFormat(learning);
        learning = Filter.useFilter(learning, filterNormlearning);
        LibSVM svm = new LibSVM();
        svm.setSVMType(new SelectedTag(LibSVM.SVMTYPE_ONE_CLASS_SVM, LibSVM.TAGS_SVMTYPE));
        svm.buildClassifier(training);
        Evaluation eval = new Evaluation(training);
        eval.evaluateModel(svm, learning);
        //print the results of modeling
        String strSummary = eval.toSummaryString();
        System.out.println("" + strSummary);
        
//        OneClassClassifier oc = new OneClassClassifier();
//        oc.buildClassifier(traindataset);
        double []classes = new double[testdataset.size()];       
        ArrayList<Attribute> atts = new ArrayList<Attribute>(5);

			atts.add(new Attribute("letter1"));
			atts.add(new Attribute("letter2"));
			atts.add(new Attribute("P1R1"));
			atts.add(new Attribute("P1P2"));
			atts.add(new Attribute("R1P2"));
			atts.add(new Attribute("R1R2"));
			atts.add(new Attribute(name));
			Instances dataRaw = new Instances("TestInstances", atts, 6);
			
			
			for(Key row:testdata) {
				double[] raw = new double[6];
					raw[0] = row.getLetter1();
					raw[1] = row.getLetter2();
					raw[2] = row.getPress1_press2();
					raw[3] = row.getPress1_release1();
					raw[4] = row.getRelease1_press2();
					raw[5] = row.getRelease1_release2();
				dataRaw.add(new DenseInstance(6, raw));
	        }
			
			if (dataRaw.classIndex() == -1) {
	            System.out.println("reset index...");
	            dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
	        }
//			Normalize filterNormdataRaw = new Normalize();
//	        filterNormdataRaw.setInputFormat(dataRaw);
//	        dataRaw = Filter.useFilter(dataRaw, filterNormdataRaw);
			for(int i=0; i<dataRaw.size()-1; i++) {
				 Instance instance = dataRaw.get(i);				 
				 classes[i] = svm.classifyInstance(instance); //
				 System.out.println(", predicted: " + training.classAttribute().value((int) classes[i]));
	        }
		//Print results of classification
//        for(Double i:classes) {
//        	System.out.println("Class is " + i);
//        }
	}
	
    public void saveTrainARFF (Vector <Key> vD, String filename){
        try{         
            PrintWriter fw = new PrintWriter(filename + ".arff");
                 fw.flush();
                 fw.println("@RELATION keys");
                 fw.println("@ATTRIBUTE letter1  NUMERIC");
                 fw.println("@ATTRIBUTE letter2  NUMERIC");
                 fw.println("@ATTRIBUTE P1R1  NUMERIC");
                 fw.println("@ATTRIBUTE P1P2  NUMERIC");
                 fw.println("@ATTRIBUTE R1P2  NUMERIC");
                 fw.println("@ATTRIBUTE R1R2  NUMERIC");
                 fw.println();
                 fw.println("@ATTRIBUTE class {'" + name + "'}");
                 
                 fw.println();
                 fw.println("@DATA");
                 
                 for(int i=0; i<vD.size(); i++){
            	   Key a = vD.get(i);
            	   fw.println(a.getLetter1() + "," + a.getLetter2() + "," + a.getPress1_press2() + "," + a.getPress1_release1() + "," + a.getRelease1_press2() + "," + a.getRelease1_release2() + "," + name);
                }
                fw.close();
        } catch(Exception ex){}finally{
            
        }
        
    }
    public void saveTestARFF (Vector <Key> vD, String filename){
        try{
         
            PrintWriter fw = new PrintWriter(filename + ".arff");
                 fw.flush();
                 fw.println("@RELATION keys");
                 fw.println("@ATTRIBUTE letter1  NUMERIC");
                 fw.println("@ATTRIBUTE letter2  NUMERIC");
                 fw.println("@ATTRIBUTE P1R1  NUMERIC");
                 fw.println("@ATTRIBUTE P1P2  NUMERIC");
                 fw.println("@ATTRIBUTE R1P2  NUMERIC");
                 fw.println("@ATTRIBUTE R1R2  NUMERIC");
                 fw.println();
                 fw.println("@ATTRIBUTE class {'" + name + "'}");
                 fw.println();
                 fw.println("@DATA");
                 
                 for(int i=0; i<vD.size(); i++){
            	   Key a = vD.get(i);
            	   fw.println(a.getLetter1() + "," + a.getLetter2() + "," + a.getPress1_press2() + "," + a.getPress1_release1() + "," + a.getRelease1_press2() + "," + a.getRelease1_release2() + ", ?");
                }
                fw.close();
        } catch(Exception ex){}finally{
            
        }
        
    }
    
	public void saveModelToFile(String filename, LibSVM svm) {
		try {
			weka.core.SerializationHelper.write(filename, svm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public LibSVM loadModelFromFile(String filename) {
		LibSVM model = new LibSVM();
		try {
			model = (LibSVM) weka.core.SerializationHelper.read(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}
}
