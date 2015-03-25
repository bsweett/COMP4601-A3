package edu.carleton.comp4601.assignment3.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Review {

	private String id;
	private String userId;
	private String helpfulness;
	private String profileName;
	private String score;
	private long time;
	private String summary;
	private String content;
	
	private ArrayList<Integer> categoryCounts;
	
	public Review(String id, String user, String content) {
		this.id = id;
		this.userId = user;
		this.content = content;
		this.setCategoryCounts(new ArrayList<Integer>());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser() {
		return userId;
	}

	public void setUser(String user) {
		this.userId = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getHelpfulness() {
		return helpfulness;
	}

	public void setHelpfulness(String helpfulness) {
		this.helpfulness = helpfulness;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public ArrayList<Integer> getCategoryCounts() {
		return categoryCounts;
	}

	public void setCategoryCounts(ArrayList<Integer> categoryCounts) {
		this.categoryCounts = categoryCounts;
	}
	
	public String getDateFromUnixTS() {
		Date date =  new Date(this.time*1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); 
		return sdf.format(date);
	}

	public String toString() {
		return this.id + "\n" + this.getUser() + "\n" + this.getContent();
	}
	
	public String toHTML() {
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<div>");
		htmlBuilder.append("<h5>"+ this.getUser() + "</h5>");
		htmlBuilder.append("<p>" + this.getDateFromUnixTS() + "</p>");
		htmlBuilder.append("<p>"+ this.getContent()+ "</p>");
		htmlBuilder.append("</div>");
		return htmlBuilder.toString();
	}
}
