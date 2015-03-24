package edu.carleton.comp4601.assignment3.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.carleton.comp4601.assignment3.algorithms.Apriori;
import edu.carleton.comp4601.assignment3.dao.Page;
import edu.carleton.comp4601.assignment3.dao.Review;
import edu.carleton.comp4601.assignment3.dao.Transaction;
import edu.carleton.comp4601.assignment3.dao.User;

public class DataParser {

	private String rootDataPath;
	private final String userPath = "users/";
	private final String pagesPath = "pages/";
	private final String reviewsPath = "reviews/";
	private final String retailPath = "retail/retail.dat";
	private final String graphPath = "graph/";

	public DataParser(String rootDataPath) {
		this.rootDataPath = rootDataPath;
	}

	/**
	 * Parses all the data for assignment 3
	 * 
	 * @return true if complete false if an exception
	 */
	public boolean parseAssignment3Content() {

		try {
			parseUsers();
			parsePages();
			parseReviews();
			parseSocialGraph();
			SocialGraph.getInstance().setA3Ready(true);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * Parses all the data for assignment 4
	 * 
	 * @return true if complete false if an exception
	 */
	public boolean parseAssignment4Content() {
		
		try {
			parseRetailData();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	// Assignment 3
	
	/**
	 * Parses the user HTML files. Each users file has a bunch of links to pages
	 * Adds the user to a HashMap of users.
	 * 
	 * @throws IOException
	 */
	private void parseUsers() throws IOException {

		File dir = new File(rootDataPath + userPath);
		System.out.println(dir.getAbsolutePath());
		File[] list = dir.listFiles();
		
		for(File file : list) {
			Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");			
			Elements links = doc.select("a[href]");
			User user = new User(doc.title(), -1);

			for(Element link : links) {
				Page page = new Page(link.text());
				user.addVisitedPage(page);
			}

			SocialGraph.getInstance().addUser(user);
		}
	}

	/**
	 * Parses the Pages HTML files. Each Page has a bunch of reviews with user IDs that links to
	 * a users profile with a list of pages they have reviewed. Each review has content in multiple
	 * paragraphs. Adds the page to a HashMap of pages.
	 * 
	 * @throws IOException
	 */
	private void parsePages() throws IOException {

		File dir = new File(rootDataPath + pagesPath);
		System.out.println(dir.getAbsolutePath());
		File[] list = dir.listFiles();

		for(File file : list) {
			Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");
			Elements links = doc.select("a[href]");
			Page page = new Page(doc.title().trim());
						
			for(Element link : links) {
				
				// Review content is left empty here because they can have multiple <p> content
				// We can always get the content from the reviews parsing below if we need it in out pages
				Review review = new Review("", link.text(), "");
				page.addReview(review);
				
			}

			SocialGraph.getInstance().addPage(page);
		}
	}

	/**
	 * Parse the Reviews HTML files. Each review has a bunch of meta tags that describe it as well as a
	 * userId as its title. Its content is stored in multiple paragraphs. Adds the review to a HashMap of
	 * Reviews.
	 * 
	 * @throws IOException
	 */
	private void parseReviews() throws IOException {

		File dir = new File(rootDataPath + reviewsPath);
		System.out.println(dir.getAbsolutePath());
		File[] list = dir.listFiles();

		for(File file : list) {
			Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");			
			Elements paragraphs = doc.select("p");
			Elements metas = doc.select("meta");
			
			String content = "";
			for(Element p : paragraphs) {
				content += p.text();
			}
			

			String userId = "";			
			String profileName = "";
			String helpfulness = "";
			String score = "";
			Long time = 0L;
			String summary = "";
			
			for(Element meta : metas) {
				String name = meta.attr("name");
				
				if(name.equals("userId")) {
					userId = meta.attr("content");
				} else if(name.equals("profileName")) {
					profileName = meta.attr("content");
				} else if(name.equals("helpfulness")) {
					helpfulness = meta.attr("content");
				} else if(name.equals("score")) {
					score = meta.attr("content");
				} else if(name.equals("time")) {
					String timeAsString = meta.attr("content");
					
					try {
						time = Long.valueOf(timeAsString).longValue();
					} catch (NumberFormatException e) {}
							
				} else if(name.equals("summary")) {
					summary = meta.attr("content");
				}
			}
			
			Review review = new Review(doc.title().trim(), userId, content);
			review.setProfileName(profileName);
			review.setHelpfulness(helpfulness);
			review.setScore(score);
			review.setTime(time);
			review.setSummary(summary);
			
			SocialGraph.getInstance().addReview(review);
		}
	}

	/**
	 * Parses a the graph HTML files. Each file is a user (ID in title) and has links to other users they are
	 * friends with. We create a multiple edge graph with the user IDs as vertices with edges to all the other
	 * users they are friends with.
	 * 
	 * @throws IOException
	 */
	private void parseSocialGraph() throws IOException {

		File dir = new File(rootDataPath + graphPath);
		System.out.println(dir.getAbsolutePath());
		File[] list = dir.listFiles();

		for(File file : list) {
			Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");	
			//Elements links = doc.select("a[href]");
			Elements users = doc.select("a");
			
			User user = new User(doc.title(), -1);
			SocialGraph.getInstance().addNewUserVertex(user);
			
			for(Element em : users) {
				
				User temp = new User(em.text(), -1);
				SocialGraph.getInstance().addNewUserVertex(temp);
				SocialGraph.getInstance().addNewDefaultEdge(user, temp);
				
			}
		}
	}
	
	// Assignment 4
	
	/**
	 * Parses the retail data set line by line. Each line is a transaction with a list of items. Build a list
	 * of Transactions with items. Each Transaction has an id (a counter).
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void parseRetailData() throws FileNotFoundException, IOException {
		
		File retailFile = new File(rootDataPath + retailPath);
		
		try (BufferedReader br = new BufferedReader(new FileReader(retailFile))) {
		    String line;
		    int counter = 0;
		    
		    while ((line = br.readLine()) != null) {
		       String[] splitArray = line.split("\\s+");
		       
		       ArrayList<Integer> tempList = new ArrayList<Integer>(splitArray.length);
		       for(String i : splitArray) {
		    	   int itemId = Integer.parseInt(i);
		    	   tempList.add(itemId);
		       }
		       
		       Transaction tr = new Transaction(counter, tempList);
		       counter++;
		       
		       SocialGraph.getInstance().addTransaction(tr);
		    }
		}
	}
}
