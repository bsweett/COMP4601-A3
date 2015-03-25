package edu.carleton.comp4601.assignment3.dao;

import java.util.ArrayList;

import edu.carleton.comp4601.assignment3.util.Category;

public class Page {

	private String title;
	private ArrayList<Review> reviews;
	private Category mainCategory;
	private Category secondaryCategory;
	private String path;

	public Page(String title) {
		this.title = title;
		this.path = "pages/" + title + ".html";
		this.reviews = new ArrayList<Review>();
		this.mainCategory = Category.NONE;
		this.secondaryCategory = Category.NONE;
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
	
	public Review getPageReviewContentJoined(Review review) {
		String reviewTitle = review.getId();
		
		if(getTitle().equals(reviewTitle)) {
			String userId = review.getUser();
			
			for(Review searchReview : getReviews()) {
				if(searchReview.getId().equals(userId)) {
					searchReview.setId(reviewTitle);
					searchReview.setContent(review.getContent());
					return searchReview;
				}
			}
		}
		
		return null;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Category getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(Category mainCategory) {
		this.mainCategory = mainCategory;
	}

	public Category getSecondaryCategory() {
		return secondaryCategory;
	}

	public void setSecondaryCategory(Category secondaryCategory) {
		this.secondaryCategory = secondaryCategory;
	}
	
	public String toString() {
		return this.title + " " + this.mainCategory.toString() + " " + this.secondaryCategory.toString();
	}
	
	public String toHTML() {
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<div style=\"float: left; width: 800px;\">");
		htmlBuilder.append("<h2>" + this.getTitle() + "</h2>");
		htmlBuilder.append("<h4> Genre: </h4>");
		htmlBuilder.append("<p>"+ this.mainCategory.toString() + "</p>");
		
		if(this.secondaryCategory != Category.NONE) {
			htmlBuilder.append("<p>"+ this.secondaryCategory.toString() + "</p>");
		}
		
		for(Review review : this.reviews) {
			htmlBuilder.append(review.toHTML());
		}
		
		htmlBuilder.append("</div>");
		return htmlBuilder.toString();
	}
}
