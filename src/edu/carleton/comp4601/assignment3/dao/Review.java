package edu.carleton.comp4601.assignment3.dao;

public class Review {

	private String user;
	private String helpfulness;
	private String profileName;
	private String score;
	private long time;
	private String summary;
	private String content;
	
	public Review(String user, String content) {
		this.user = user;
		this.content = content;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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

	public String toString() {
		return this.getUser() + "\n" + this.getContent();
	}
}
