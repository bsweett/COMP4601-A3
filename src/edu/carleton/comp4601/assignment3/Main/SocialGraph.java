package edu.carleton.comp4601.assignment3.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import edu.carleton.comp4601.assignment3.algorithms.Cluster;
import edu.carleton.comp4601.assignment3.dao.Advertisement;
import edu.carleton.comp4601.assignment3.dao.Page;
import edu.carleton.comp4601.assignment3.dao.Review;
import edu.carleton.comp4601.assignment3.dao.Rule;
import edu.carleton.comp4601.assignment3.dao.Transaction;
import edu.carleton.comp4601.assignment3.dao.User;
import edu.carleton.comp4601.assignment3.util.AdvertisementFactory;
import edu.carleton.comp4601.assignment3.util.Category;
import edu.carleton.comp4601.assignment3.util.Utils;

public class SocialGraph {

	private Multigraph<String, DefaultEdge> graph;
	private ConcurrentHashMap<String, User> users;
	private ConcurrentHashMap<String, Page> pages;
	private ArrayList<Review> reviews;
	private AdvertisementFactory adFactory;
	private List<Cluster> clusters;
	private ArrayList<Rule> rules;
	private ConcurrentHashMap<Integer, Transaction> transactions;
	
	private boolean a3Ready = false;
	private boolean a4Ready = false;
	private boolean a4RulesReady = false;
	private boolean contextReady = false;
	private boolean communityReady = false;
	
	private final int CONFIDENCE = 50;
	
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
		this.adFactory = new AdvertisementFactory();
		this.reviews = new ArrayList<Review>();
		this.clusters = new ArrayList<Cluster>();
		this.transactions = new ConcurrentHashMap<Integer, Transaction>();
		this.rules = new ArrayList<Rule>();
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
		return this.reviews.add(review);
	}
	
	public synchronized void clearAssignment3Data() {
		this.graph = new Multigraph<String, DefaultEdge> (DefaultEdge.class);
		this.users = new ConcurrentHashMap<String, User>();
		this.pages = new ConcurrentHashMap<String, Page>();
		this.reviews = new ArrayList<Review>();
		this.a3Ready = false;
		this.contextReady = false;
	}
	
	public synchronized boolean addTransaction(Transaction tr) {
		return this.transactions.put(tr.getId(), tr) != null;
	}
	
	public synchronized void clearAssignment4Data() {
		this.transactions = new ConcurrentHashMap<Integer, Transaction>();
		this.a4Ready = false;
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
	
	public void setA3Ready(boolean ready) {
		this.a3Ready = ready;
	}
	
	public void setA4Ready(boolean ready) {
		this.a4Ready = ready;
	}
	
	public void setContextReady(boolean ready) {
		this.contextReady = ready;
	}
	
	public boolean isCommunityReady() {
		return communityReady;
	}

	public void setCommunityReady(boolean communityReady) {
		this.communityReady = communityReady;
	}

	public boolean isA3ParseFinished() {
		return this.a3Ready;
	}
	
	public boolean isA4ParseFinished() {
		return this.a4Ready;
	}
	
	public boolean isContextReady() {
		return this.contextReady;
	}
	
	public Multigraph<String, DefaultEdge> getGraph() {
		return this.graph;
	}
	
	public ConcurrentHashMap<Integer, Transaction> getTransactions() {
		return this.transactions;
	}
	
	public ConcurrentHashMap<String, Page> getPages() {
		return this.pages;
	}
	
	public ArrayList<Review> getReviews() {
		return this.reviews;
	}
	
	public ConcurrentHashMap<String, User> getUsers() {
		return this.users;
	}

	public ArrayList<Rule> getRules() {
		return rules;
	}

	public void setRules(ArrayList<Rule> rules) {
		this.rules = rules;
	}
	
	//Finds the rules that match the given product array
	public ArrayList<int[]> giveSuggestions(int[] products) {
		ArrayList<int[]> results = new ArrayList<int[]>();
		Arrays.sort(products);
		for(Rule r: this.rules) {
			if(Arrays.equals(r.getSetA(), products) && r.getConfidence() >= CONFIDENCE) {
				results.add(r.getSetB());
			}
		}
		return results;
	}
	
	public User getUserByName(String name) {
		return this.users.get(name);
	}
	
	public Page getPageByName(String name) {
		return this.pages.get(name);
	}
	
	public void setCommunity(List<Cluster> clusters) {
		this.clusters = clusters;
	}
	
	public List<Cluster> getCommunity() {
		return this.clusters;
	}
	
	public AdvertisementFactory getAdFactory() {
		return this.adFactory;
	}
	
	public String getAdvertForUserAndPage(User user, Page page) {
		Cluster cluster = this.clusters.get(user.getCluster());
		
		ArrayList<Double> ratings = cluster.getRatingTotals();
		
		int[] results = Utils.findTopTwoGenresForCluster(ratings);
		System.out.println(results[0]);
		System.out.println(results[1]);
		
		Category mainClusterCat = Category.fromInteger(results[0]);
		Category secondClusterCat = Category.fromInteger(results[1]);
		Category pageCat = page.getMainCategory();
		
		Advertisement ad = null;
		
		System.out.println("MC: " + mainClusterCat.toString());
		System.out.println("SC: " + secondClusterCat.toString());
		System.out.println("Page: " + page.toString());
		
		if((pageCat == mainClusterCat) || (pageCat == secondClusterCat)) {
			// page cat
			ad = this.adFactory.getAdForCategories(pageCat, Category.NONE);
		} else { 
			// main cluster combined page cat
			ad = this.adFactory.getAdForCategories(mainClusterCat, pageCat);
		}
		
		if (ad == null) {
			return "Error: No Ad";
		}
		
		return ad.getAdvert();
	}

	public boolean isA4RulesReady() {
		return a4RulesReady;
	}

	public void setA4RulesReady(boolean a4RulesReady) {
		this.a4RulesReady = a4RulesReady;
	}
}
