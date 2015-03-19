package edu.carleton.comp4601.assignment3.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.carleton.comp4601.assignment3.dao.Page;
import edu.carleton.comp4601.assignment3.dao.Review;
import edu.carleton.comp4601.assignment3.dao.User;

public class DataParser {

	private String rootDataPath;
	private final String userPath = "users/";
	private final String pagesPath = "pages/";
	private final String reviewsPath = "reviews/";
	private final String graphPath = "";

	public DataParser(String rootDataPath) {
		this.rootDataPath = rootDataPath;
	}

	public boolean parseContent() {

		try {
			parseUsers();
			parsePages();
			parseReviews();
			parseSocialGraph();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

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

	// TODO: Might be better to not store Reviews here because of their meta tags
	// Think about how to add reviews to pages after parsing and adding pages to users after
	private void parsePages() throws IOException {

		File dir = new File(rootDataPath + pagesPath);
		System.out.println(dir.getAbsolutePath());
		File[] list = dir.listFiles();

		for(File file : list) {
			Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");			
			Elements links = doc.select("a[href]");
			Elements paragraphs = doc.select("p");
			Page page = new Page(doc.title());
			
			ArrayList<String> reviewUsers = new ArrayList<String>();
			ArrayList<String> contents = new ArrayList<String>();
			for(Element link : links) {
				reviewUsers.add(link.text());
			}
			
			for(Element p : paragraphs) {
				contents.add(p.text());
			}
			
			for(int i = 0; i < reviewUsers.size(); i++) {
				Review review = new Review(reviewUsers.get(i), contents.get(i));
				page.addReview(review);
			}
			
			SocialGraph.getInstance().addPage(page);
		}
	}

	private void parseReviews() throws IOException {

		File dir = new File(rootDataPath + reviewsPath);
		System.out.println(dir.getAbsolutePath());
		File[] list = dir.listFiles();

		for(File file : list) {
			Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");			
			Elements paragraphs = doc.select("p");
			Elements metas = doc.select("meta");
			
			String content = "";
			if(!paragraphs.isEmpty()) {
				content = paragraphs.first().text();
			}
			
			// TODO: Do we need to parse these? Update model if so
			String userId = "";
			String profileName = "";
			String helpfulness = "";
			String score = "";
			String timeAsString = "";
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
					timeAsString = meta.attr("content");
				} else if(name.equals("summary")) {
					summary = meta.attr("content");
				}
			}
			
			Review review = new Review(doc.title(), content);
			
			SocialGraph.getInstance().addReview(review);
		}
	}

	private void parseSocialGraph() throws IOException {

		File dir = new File(rootDataPath + graphPath);
		File[] list = dir.listFiles();

		for(File file : list) {
			// TODO: Parse Graph
		}
	}
}
