package edu.carleton.comp4601.assignment3.dao;

public class Rule {
	
	//A frequent set of products
	private int[] setA;
	//A set of recommended products based on setA
	private int[] setB;
	//The confidence of the recommendation
	private int confidence;
	
	//Defines a rule given by the Apriori algorithm
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
