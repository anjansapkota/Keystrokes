package com.um.model;

public class Result {
	private T_Test tTestResult;
	private Corelation CorrelationTestResult;
	private int matchesResult;
	private double score;
	private String resultptints;
	
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
	public int getMatchesResult() {
		return matchesResult;
	}
	public void setMatchesResult(int matchesResult) {
		this.matchesResult = matchesResult;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getResultptints() {
		return resultptints;
	}
	public void setResultptints(String resultptints) {
		this.resultptints = resultptints;
	}
}
