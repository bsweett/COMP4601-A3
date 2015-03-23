package edu.carleton.comp4601.assignment3.Main;

import java.util.concurrent.ConcurrentHashMap;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import edu.carleton.comp4601.assignment3.dao.Page;
import edu.carleton.comp4601.assignment3.dao.Review;
import edu.carleton.comp4601.assignment3.dao.Transaction;
import edu.carleton.comp4601.assignment3.dao.User;

public class SocialGraph {

	// String = username, edge is a relation to another user
	private Multigraph<String, DefaultEdge> graph;
	private ConcurrentHashMap<String, User> users;
	private ConcurrentHashMap<String, Page> pages;
	private ConcurrentHashMap<String, Review> reviews;
	private ConcurrentHashMap<Integer, Transaction> transactions;
	
	private static SocialGraph instance;
	
	public static void setInstance(SocialGraph instance) {
		SocialGraph.instance = instance;
	}
	
	public static SocialGraph getInstance() {

		if (instance == null)
			instance = new SocialGraph();
		return instance;

	}
	
	//Initialize
	public SocialGraph() {
		this.graph = new Multigraph<String, DefaultEdge> (DefaultEdge.class);
		this.users = new ConcurrentHashMap<String, User>();
		this.pages = new ConcurrentHashMap<String, Page>();
		this.reviews = new ConcurrentHashMap<String, Review>();
		this.transactions = new ConcurrentHashMap<Integer, Transaction>();
	}
	
	/**
	 * Adds a user as a vertex to the graph. This method should be called while parsing
	 * social graph data.
	 * 
	 * @param user
	 * @return
	 */
	public synchronized boolean addVertex(String name) {
		return this.graph.addVertex(name);
	}
	
	public synchronized boolean addEdge(String user1, String user2) {
		DefaultEdge edge = this.graph.addEdge(user1, user2);
		if(edge == null) {
			return false;
		}
		
		return true;
	}
	
	public synchronized boolean addUser(User user) {
		return this.users.put(user.getName(), user) != null;
	}
	
	public synchronized boolean addPage(Page page) {
		return this.pages.put(page.getTitle(), page) != null;
	}

	public synchronized boolean addReview(Review review) {
		return this.reviews.put(review.getUser(), review) != null;
	}
	
	public synchronized void clearAssignment3Data() {
		this.graph = new Multigraph<String, DefaultEdge> (DefaultEdge.class);
		this.users = new ConcurrentHashMap<String, User>();
		this.pages = new ConcurrentHashMap<String, Page>();
		this.reviews = new ConcurrentHashMap<String, Review>();
	}
	
	public synchronized boolean addTransaction(Transaction tr) {
		return this.transactions.put(tr.getId(), tr) != null;
	}
	
	public synchronized void clearAssignment4Data() {
		this.transactions = new ConcurrentHashMap<Integer, Transaction>();
	}
	

	public synchronized boolean addNewUserVertex(User user) {
		
		for(String username : getGraph().vertexSet()) {
			
			if(username == user.getName()) {
				return false;
			}
			
		}
		
		return getGraph().addVertex(user.getName());
	}
	
	public synchronized boolean addNewDefaultEdge(User user1, User user2) {
		
		if(getGraph().containsEdge(user1.getName(), user2.getName())) {
			return false;
		}
		
		return getGraph().addEdge(user1.getName(), user2.getName()) != null;
	}
	
	//TODO: Search Functions
	
	/*
	//Finds a vertex in the graph by url
	public synchronized PageVertex findVertex(String url) {
		for (PageVertex vertex : getVertices().values()) {
		    if(vertex.getUrl().equals(url)) {
		    	return vertex;
		    }
		}
		return null;
	}*/
	
	public Multigraph<String, DefaultEdge>  getGraph() {
		return this.graph;
	}
	
	public ConcurrentHashMap<Integer, Transaction>  getTransactions() {
		return this.transactions;
	}
}
