package com.um.service;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Vector;
import org.springframework.beans.factory.annotation.Autowired;
import com.um.model.Key;
import com.um.model.Usuario;
import org.apache.commons.math3.stat.inference.TTest;

public class LaboratoryServiceImpl implements LaboratoryService {
	@Autowired
	private KMS kms;
	private Vector <Object> keys4mdbEvry1 = new Vector  <Object> ();
	@Autowired
	public void test() throws SQLException {
		Vector <Usuario> personas = kms.bringlistofPersons();
		Vector<String> userNames =  new Vector<String>();
		for (int i = 0; i < personas.size(); i++) {
			Vector <Key> registrosTemp = kms.retrieveKeysFromDB(personas.get(i).getMatricula());
			for(int jj=0; jj<registrosTemp.size(); jj++) {
				if(registrosTemp.get(jj).getRelease1_press2() >= 2000000000) {
					registrosTemp.remove(jj);
				}
			}
			keys4mdbEvry1.add(registrosTemp);
			printVector(registrosTemp,personas.get(i).getNombre());
			userNames.add(personas.get(i).getNombre());
		}
	}
	
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
    
    public void tTest(Vector<Key> teclaList1, Vector<Key> teclaList2 ) {   	
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
		System.out.println("T-Test Results : " + "P1R1: " + pValuePress1_release1 + " y " + "P1P2: " + pValuePress1_press2 + "R1P2: " + pValueRelease1_press2 + "R1R2: " + pValueRelease1_release2 );
    }
	


}
