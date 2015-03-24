package edu.carleton.comp4601.assignment3.dao;

public class Rule {

	private int[] setA;
	private int[] setB;
	private int confidence;
	
	public Rule(int[] setA, int[] setB, int confidence) {
		this.setA = setA;
		this.setB = setB;
		this.confidence = confidence;
	}

	public int[] getSetA() {
		return setA;
	}

	public int[] getSetB() {
		return setB;
	}

	public int getConfidence() {
		return confidence;
	}
}
