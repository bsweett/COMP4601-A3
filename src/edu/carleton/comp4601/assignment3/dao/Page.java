package edu.carleton.comp4601.assignment3.dao;

import java.util.ArrayList;

public class Page {

	private String title;
	private ArrayList<Review> reviews;
	private String path;

	public Page(String title) {
		this.title = title;
		this.path = "pages/" + title + ".html";
		this.reviews = new ArrayList<Review>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<Review> getReviews() {
		return reviews;
	}

	public void setReviews(ArrayList<Review> reviews) {
		this.reviews = reviews;
	}
	
	public boolean addReview(Review review) {
		return getReviews().add(review);
	}
	
	public boolean hasReview(Review review) {
		for(Review searchReview : getReviews()) {
			if(searchReview.getContent().equals(review.getContent())) {
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
}
