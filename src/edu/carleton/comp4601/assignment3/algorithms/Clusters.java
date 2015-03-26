package edu.carleton.comp4601.assignment3.algorithms;

import java.util.ArrayList;
import java.util.List;

import edu.carleton.comp4601.assignment3.dao.User;

public class Clusters extends ArrayList<Cluster> {

	private static final long serialVersionUID = 1L;
	private final List<User> allUsers;
	private boolean isChanged;

	public Clusters(List<User> allUsers) {
		this.allUsers = allUsers;
	}

	public Integer getNearestCluster(User user) { 
		double minSquareOfDistance = Double.MAX_VALUE;
		int itsIndex = -1;
		
		for (int i = 0 ; i < size(); i++) {
			double squareOfDistance = user.distance(get(i).getCentroid());
			
			if (squareOfDistance < minSquareOfDistance) {
				minSquareOfDistance = squareOfDistance;
				itsIndex = i;
			}
		}
		
		return itsIndex;
	}

	public boolean updateClusters() {
		for (Cluster cluster : this) {
			cluster.updateCentroid();
			cluster.getUsers().clear();
		}
		
		isChanged = false;
		assignPointsToClusters();
		return isChanged;
	}

	public void assignPointsToClusters() {
		for (User user : this.allUsers) {
			int previousIndex = user.getLastCluster();
			int newIndex = getNearestCluster(user);
			
			if (previousIndex != newIndex) {
				this.isChanged = true;
			}
				
			Cluster target = get(newIndex);
			user.update();
			user.setCluster(newIndex);
			target.getUsers().add(user);
		}
	}
	
	public boolean sortUsersIntoClusters() {
		for (User user: allUsers) {
			
			int previousCluster = user.getLastCluster();
			int newCluster = user.getCluster();
			
			if (previousCluster != newCluster) {
				isChanged = true;
			}
			
			if(this.size() <= user.getCluster()) {
				Cluster cluster = new Cluster(user);
				this.add(cluster);
			} else {
				Cluster target = get(user.getCluster());
				target.getUsers().add(user);
			}
		}
		return isChanged;
	}
}
