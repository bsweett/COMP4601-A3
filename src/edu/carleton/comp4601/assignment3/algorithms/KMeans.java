package edu.carleton.comp4601.assignment3.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.carleton.comp4601.assignment3.dao.User;

public class KMeans {

	/*
	 * -------
	 * 04 36697
	 * 05 36191
	 * 06 35823
	 * 07 32366
	 * 08 31978
	 * 09 31174 <--
	 * 10 31864
	 * 11 32199
	 * 12 31306
	 * 13 28912
	 * -------
	 */
	public int no_clusters = 9;
	
	private static final Random random = new Random();
	public List<User> allUsers;
	private Clusters usersClusters; //the k Clusters
	
	public KMeans(ArrayList<User> users) {
		this.allUsers = users;
	}

	/*
	 * This is where your implementation goes
	 */
	public List<Cluster> run() {

		double total = 0;
		
		List<Cluster> userClusters = getPointsClusters();
		for (int p = 0 ; p < no_clusters; p++) {
			
			Cluster cluster = userClusters.get(p);
			System.out.println("Cluster " + p + ": " + cluster);
			
			User center = cluster.getCentroid();
			List<User> clusterUsers = cluster.getUsers();
			
			
			for(int k = 0; k < clusterUsers.size(); k ++) {
				User current = clusterUsers.get(k);
				total += current.distance(center);
			}
			
			// Compute the ratingTotals for each cluster by adding all the user
			// genre scores together
			cluster.computeRatingTotalsFromUsers();
		}
		
		System.out.println("Total distance: "+ total);
		return userClusters;
	}


	/**
	 * step 1: get random seeds as initial centroids of the k clusters
	 */
	private void getInitialKRandomSeeds() {
		this.usersClusters = new Clusters(allUsers);
		List<User> kRandomPoints = getKRandomPoints();
		
		for (int i = 0; i < no_clusters; i++) {
			kRandomPoints.get(i).setCluster(i);
			this.usersClusters.add(new Cluster(kRandomPoints.get(i)));
		} 
	}

	private List<User> getKRandomPoints() {
		List<User> kRandomPoints = new ArrayList<User>();
		boolean[] alreadyChosen = new boolean[allUsers.size()];
		int size = allUsers.size();
		
		for (int i = 0; i < no_clusters; i++) {
			int index = -1, r = random.nextInt(size--) + 1;
			
			for (int j = 0; j < r; j++) {
				index++;
				while (alreadyChosen[index])
					index++;
			}
			
			kRandomPoints.add(allUsers.get(index));
			alreadyChosen[index] = true;
		}
		
		return kRandomPoints;
	}

	/**
	 * step 2: assign points to initial Clusters
	 */
	private void getInitialClusters(){
		usersClusters.assignPointsToClusters();
	}

	/** 
	 * step 3: update the k Clusters until no changes in their members occur
	 */
	private void updateClustersUntilNoChange(){
		boolean unchanged = usersClusters.updateClusters();
		while (unchanged) {
			unchanged = usersClusters.updateClusters();
		}
	}

	/**
	 * do K-means clustering
	 */
	public List<Cluster> getPointsClusters() {
		if (usersClusters == null) {
			getInitialKRandomSeeds();
			getInitialClusters();
			System.out.println("Done intial");
			updateClustersUntilNoChange();
		}
		
		return usersClusters;
	}
}
