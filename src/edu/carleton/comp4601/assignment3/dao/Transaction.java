package edu.carleton.comp4601.assignment3.dao;

import java.util.ArrayList;


public class Transaction {

	private int id;
	private ArrayList<Integer> items;
	
	public Transaction(int id, ArrayList<Integer> items) {
		this.setId(id);
		this.setItems(items);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Integer> getItems() {
		return items;
	}

	public void setItems(ArrayList<Integer> items) {
		this.items = items;
	}
	
	public int numberOfItems() {
		return this.items.size();
	}
	
	public String toHTMLString() {
		
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<h3>" + this.getId() + "</h3>");
		htmlBuilder.append("<h5> Items </h5>");
		htmlBuilder.append("<ul>");
		
		for (Integer i : this.getItems()) {
			htmlBuilder.append("<li>");
			htmlBuilder.append(i.toString());
			htmlBuilder.append("</li>");
		}
		
		htmlBuilder.append("</ul>");
		
		return htmlBuilder.toString();
	}
}
