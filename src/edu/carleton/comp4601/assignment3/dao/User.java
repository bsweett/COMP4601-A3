package edu.carleton.comp4601.assignment3.dao;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.carleton.comp4601.assignment3.util.Category;

public class User {

	private String name;
	private ArrayList<Category> allFeatures;
	private Set<Category> uniqueFeatures;
	private Integer cluster;
	private ArrayList<Page> visitedPages;
	private Set<Review> reviews;
	private String path;

	public User(String name, Integer cluster) {
		this.name = name;
		this.cluster = cluster;
		this.path = "users/" + name + ".html";
		this.visitedPages = new ArrayList<Page>();
		this.uniqueFeatures = new HashSet<Category>();
		this.setAllFeatures(new ArrayList<Category>());
		this.setReviews(new HashSet<Review>());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCluster() {
		return cluster;
	}

	public void setCluster(Integer cluster) {
		this.cluster = cluster;
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

	public boolean setVisitedPageAtIndex(int index, Page page) {
		return this.visitedPages.set(index, page) != null;
	}

	public int getIndexOfVisitedPage(Page page) {
		int count = 0;
		for(Page searchPage : getVisitedPages()) {
			if(searchPage.getTitle().equals(page.getTitle())) {
				return count;
			}
			count++;
		}

		return -1;
	}

	/**
	 * Builds a list of all features (Categories) from all the pages
	 * they have viewed. Also builds a list of unique features.
	 * 
	 */
	public void buildFeatureList() {

		for(Page page : this.visitedPages) {

			Category main = page.getMainCategory();
			Category second = page.getSecondaryCategory();

			if(main != Category.NONE) {
				addUniqueFeature(main);
				addFeature(main);
			}

			if(second != Category.NONE) {
				addUniqueFeature(second);
				addFeature(second);
			}

		}

	}

	/**
	 * Gets the top category the user has reviewed. Returns NONE
	 * if no top is found.
	 * 
	 * @return Category the users top Category
	 */
	public Category getTopCategory() {

		Map<Category, Integer> counts = new HashMap<Category, Integer>();
		
		for(Category c : this.allFeatures) {
			counts.put(c, Collections.frequency(this.allFeatures, c));
		}
		
		Entry<Category,Integer> maxEntry = new AbstractMap.SimpleEntry<Category, Integer>(Category.NONE, 0);

		for(Entry<Category,Integer> entry : counts.entrySet()) {
		    if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
		        maxEntry = entry;
		    }
		}
		
		return maxEntry.getKey();
	}

	/**
	 * Returns the total number of reviews the user has left.
	 * NOTE: This number is not unique (i.e. more than one review per page)
	 * 
	 * @return
	 */
	public int getTotalReviews() {
		return this.visitedPages.size();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Set<Category> getUniqueFeatures() {
		return uniqueFeatures;
	}

	public void setUniqueFeatures(Set<Category> features) {
		this.uniqueFeatures = features;
	}

	public boolean addUniqueFeature(Category genre) {
		return getUniqueFeatures().add(genre);
	}

	public ArrayList<Category> getAllFeatures() {
		return allFeatures;
	}

	public void setAllFeatures(ArrayList<Category> allFeatures) {
		this.allFeatures = allFeatures;
	}

	public boolean addFeature(Category genre) {
		return this.allFeatures.add(genre);
	}

	public Set<Review> getReviews() {
		return reviews;
	}

	public void setReviews(HashSet<Review> reviews) {
		this.reviews = reviews;
	}
	
	public boolean addReview(Review review) {
		return this.reviews.add(review);
	}

	public String toString() {
		return this.name + " : " + this.getTopCategory().toString() + " : " + this.path + " : " + this.cluster;
	}
	
	public String toHTML() {
		
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<div>");
		htmlBuilder.append("<h3>" + this.getName() + "</h3>");
		htmlBuilder.append("<h5> User Info </h5>");
		htmlBuilder.append("<p>" + "Favourite Genre: " + this.getTopCategory().toString() + "</p>");
		htmlBuilder.append("<p>" + "# of movies seen: " + this.getTotalReviews() + "</p>");
		htmlBuilder.append("<h5> Movies Seen </h5>");
		htmlBuilder.append("<ul>");
		
		for (Page page : this.visitedPages) {
			htmlBuilder.append("<li>");
			htmlBuilder.append(page.toString());
			htmlBuilder.append("</li>");
		}
		
		htmlBuilder.append("</ul>");
		
		/*
		htmlBuilder.append("<h5> Reviews </h5>");
		htmlBuilder.append("<ul>");
		
		for (Review review : this.reviews) {
			htmlBuilder.append("<li>");
			htmlBuilder.append(review.toString());
			htmlBuilder.append("</li>");
		}
		
		htmlBuilder.append("</ul>");*/
		htmlBuilder.append("</div>");
		return htmlBuilder.toString();
	}

}
