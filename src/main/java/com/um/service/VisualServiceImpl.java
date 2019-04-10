package com.um.service;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import org.springframework.stereotype.Service;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

@Service("visualizerService")
public class VisualServiceImpl implements VisualService {
	
	@Override
	public void visualize(Evaluation eval ) throws Exception {
	      ThresholdCurve tc = new ThresholdCurve();
	      int classIndex = 0;
	      Instances result = tc.getCurve(eval.predictions(), classIndex);
	      // plot curve
	      ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
	      vmc.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(result), 4) + ")");
	      vmc.setName(result.relationName());
	      PlotData2D tempd = new PlotData2D(result);
	      tempd.setPlotName(result.relationName());
	      tempd.addInstanceNumberAttribute();
	      // specify which points are connected
	      boolean[] cp = new boolean[result.numInstances()];
	      for (int n = 1; n < cp.length; n++) cp[n] = true;
	      tempd.setConnectPoints(cp);
	      // add plot
	      vmc.addPlot(tempd);
	      // display curve
	      String plotName = vmc.getName();
	      JFrame jf = new JFrame(plotName);
	      jf.setSize(500, 400);
	      jf.setLocationRelativeTo(null);
	      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      jf.getContentPane().setLayout(new BorderLayout());
	      jf.getContentPane().add(vmc, BorderLayout.CENTER);
	      jf.addWindowListener(new java.awt.event.WindowAdapter() {

	     public void windowClosing(java.awt.event.WindowEvent e) {
	              jf.dispose();
	          }
	      });
	      jf.setVisible(true);
	  }	
}
