package com.um.model;

public class Result {
	private T_Test tTestResult;
	private Corelation CorrelationTestResult;
	private double regressionResult;
	private int matchesResult;
	private int KmeansResult;
	
	public T_Test gettTestResult() {
		return tTestResult;
	}
	public void settTestResult(T_Test tTestResult) {
		this.tTestResult = tTestResult;
	}
	public Corelation getCorrelationTestResult() {
		return CorrelationTestResult;
	}
	public void setCorrelationTestResult(Corelation correlationTestResult) {
		CorrelationTestResult = correlationTestResult;
	}
	public double getRegressionResult() {
		return regressionResult;
	}
	public void setRegressionResult(double regressionResult) {
		this.regressionResult = regressionResult;
	}
	public int getMatchesResult() {
		return matchesResult;
	}
	public void setMatchesResult(int matchesResult) {
		this.matchesResult = matchesResult;
	}
	public int getKmeansResult() {
		return KmeansResult;
	}
	public void setKmeansResult(int kmeansResult) {
		KmeansResult = kmeansResult;
	}

}
