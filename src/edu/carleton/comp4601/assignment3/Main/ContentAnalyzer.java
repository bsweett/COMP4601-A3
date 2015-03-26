package edu.carleton.comp4601.assignment3.Main;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.carleton.comp4601.assignment3.dao.Page;
import edu.carleton.comp4601.assignment3.dao.Review;
import edu.carleton.comp4601.assignment3.dao.User;
import edu.carleton.comp4601.assignment3.util.Category;
import edu.carleton.comp4601.assignment3.util.Utils;

public class ContentAnalyzer {

	private SocialGraph data;
	private final Category[] allValues = Category.values();
	private boolean contantAnalyzed;

	public ContentAnalyzer() {

	}

	public void setDataGraph(SocialGraph data) {
		this.data = data;
		this.contantAnalyzed = false;
	}

	public SocialGraph run() {
		if(this.data != null) {
			findReviewsCategories();
			joinReviewsAndPages();
			findPagesCategories();
			joinUsersAndPages();
		}
		
		this.data.setContextReady(true);
		return this.data;
	}

	/**
	 * Creates a list of counts of instances of popular categories per review content.
	 * Stores a copy of these counts in the review. We ignore the none value by subtracting
	 * 1 from the length (its the last time in the values array).
	 * 
	 */
	private void findReviewsCategories() {

		int numOfReviews = 0;
		
		for(Review review : data.getReviews()) {
			
			String content = review.getContent();
			
			ArrayList<Integer> categoryCounts = new ArrayList<Integer>(allValues.length - 1);
			
			for(int i = 0; i < allValues.length - 1; i++) {

				int count = 0;
				Pattern p = Pattern.compile(allValues[i].toString().toLowerCase());
				Matcher m = p.matcher(content.toLowerCase());
				while (m.find()) {
					count += 1;
				}

				categoryCounts.add(count);
			}
			
			review.setCategoryCounts(categoryCounts);
			
			numOfReviews++;
		}
		
		System.out.println("# of Reviews:" + numOfReviews);
	}

	/**
	 * Cleans up the data set by adding all of the reviews with a given id of a page into
	 * their page's review list. This overwrites any of the review data parsed from page 
	 * parsing and uses the review content from the reviews parsing.
	 * 
	 */
	private void joinReviewsAndPages() {

		for(Entry<String, Page> entry : data.getPages().entrySet()) {
			Page page = entry.getValue();
			ArrayList<Review> reviews = new ArrayList<Review>();

			for(Review review : data.getReviews()) {

				if(page.getTitle().equalsIgnoreCase(review.getId())) {
					reviews.add(review);
				}
				
			}
			
			page.setReviews(reviews);
			data.getPages().put(page.getTitle(), page);
		}
		
		System.out.println("# of pages:" + data.getPages().entrySet().size());
	}

	/**
	 * For every page we get all the reviews on the page and total up the score from every review categories
	 * list. Then we take the top 2 results from the scores and set them as our content's main and secondary
	 * category. Finally update the pages data.
	 * 
	 */
	private void findPagesCategories() {
		
		for(Entry<String, Page> entry : data.getPages().entrySet()) {
			Page page = entry.getValue();
			
			ArrayList<Integer> categoryCountsPerPage = new ArrayList<Integer>(allValues.length - 1);
			while(categoryCountsPerPage.size() < allValues.length - 1) {
				categoryCountsPerPage.add(0);
			}

			for(Review review : page.getReviews()) {
				ArrayList<Integer> counts = review.getCategoryCounts();
				
				int count = 0;
				for(Integer i : counts) {
					int current = categoryCountsPerPage.get(count);
					categoryCountsPerPage.set(count, (current + i));
					count++;
				}
			}
			
			int[] results = Utils.findTopTwoGenresForPages(categoryCountsPerPage);
			page.setMainCategory(Category.fromInteger(results[0]));
			page.setSecondaryCategory(Category.fromInteger(results[1]));	
			
			System.out.println(page.toString());
			
			data.getPages().put(page.getTitle(), page);
		}
	}

	/**
	 * Loops through all the users and checks to see if they have visited a given page. If they have
	 * we update the page object so that it will contain all of the page content (reviews and such) and
	 * not the empty shell we used from parsing.
	 * 
	 */
	private void joinUsersAndPages() {
		
		for(Entry<String, User> entry : data.getUsers().entrySet()) {
			User user = entry.getValue();
			
			// Join Pages
			for(Entry<String, Page> entry2 : data.getPages().entrySet()) {
				Page page = entry2.getValue();
				int didVisit = user.getIndexOfVisitedPage(page);
				
				if(didVisit != -1) {
					user.setVisitedPageAtIndex(didVisit, page);
				}
			}
			
			// Join Reviews
			for(Review review : data.getReviews()) {
				
				if(review.getUser().equalsIgnoreCase(user.getName())) {
					user.addReview(review);
				}
				
			}
			
			user.buildFeatureList();
			data.getUsers().put(user.getName(), user);
		}
	}
	
	public boolean isContentAnalzyed() {
		return this.contantAnalyzed;
	}
	
}
