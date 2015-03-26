package edu.carleton.comp4601.assignment3.dao;

import java.util.ArrayList;
import java.util.Random;

import edu.carleton.comp4601.assignment3.util.Category;

public class Advertisement {

	private final int min = 0;
	private final int max = 2;
	private Category mainGenre;
	private Category secondGenre;
	private ArrayList<String> advertStrings;
	
	public Advertisement(Category main, Category second) {
		this.mainGenre = main;
		this.secondGenre = second;
		this.advertStrings = new ArrayList<String>(max + 1);
		
		if(this.secondGenre != Category.NONE) {
			this.advertStrings.add("This is an advertisement for " + this.mainGenre.toString() + 
					" and " + this.secondGenre.toString() + "!");
			this.advertStrings.add("This is another advertisement considering " + this.mainGenre.toString() + 
					" and " + this.secondGenre.toString() + "!");
			this.advertStrings.add("This is some ad for " + this.mainGenre.toString() + 
					" and " + this.secondGenre.toString() + "!");
		} else {
			this.advertStrings.add("This is an advertisement for " + this.mainGenre.toString() + "!");
			this.advertStrings.add("This is another advertisement considering " + this.mainGenre.toString() + "!");
			this.advertStrings.add("This is some ad for " + this.mainGenre.toString() + "!");
		}
	}
	
	public Category getMainGenre() {
		return this.mainGenre;
	}
	
	public Category getSecondGenre() {
		return this.secondGenre;
	}
	
	public String getAdvert() {
		Random r = new Random();
		int index = r.nextInt(max - min + 1) + min;
		return this.advertStrings.get(index);
	}
	
	public String toHTML() {
		
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<div>");
		htmlBuilder.append("<h5> Advertisement: " + this.mainGenre.toString() + " " + this.secondGenre.toString() + " </h5>");
		
		for(String s : this.advertStrings) {
			htmlBuilder.append("<p>" + s + "</p>");
		}
		
		htmlBuilder.append("</div>");
		return htmlBuilder.toString();
	}
}
