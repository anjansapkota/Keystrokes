package com.um.service;

import weka.classifiers.Evaluation;

public interface VisualService {

	public void visualize(Evaluation eval) throws Exception;
}