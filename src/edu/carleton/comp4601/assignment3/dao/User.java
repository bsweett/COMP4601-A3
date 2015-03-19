package edu.carleton.comp4601.assignment3.dao;

import java.util.ArrayList;

public class User {

	private String name;
	private Integer category;
	private ArrayList<Page> visitedPages;
	private String path;
	
	public User(String name, Integer category) {
		this.name = name;
		this.category = category;
		this.path = "users/" + name + ".html";
		this.visitedPages = new ArrayList<Page>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public ArrayList<Page> getVisitedPages() {
		return visitedPages;
	}

	public void setVisitedPages(ArrayList<Page> visitedPages) {
		this.visitedPages = visitedPages;
	}
	
	public boolean addVisitedPage(Page page) {
		return getVisitedPages().add(page);
	}
	
	public boolean didVisitPage(Page page) {
		for(Page searchPage : getVisitedPages()) {
			if(searchPage.getTitle().equals(page.getTitle())) {
				return true;
			}
		}
		
		return false;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String toString() {
		return this.name + " : " + this.path + " : " +this.category;
	}
	
}
