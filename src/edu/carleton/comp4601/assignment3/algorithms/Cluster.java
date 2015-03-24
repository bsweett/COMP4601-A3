package edu.carleton.comp4601.assignment3.algorithms;

import java.util.ArrayList;
import java.util.List;

import edu.carleton.comp4601.assignment3.dao.User;

public class Cluster {

	private final List<User> users;
	private User centroid;

	public Cluster(User firstUser) {
		users = new ArrayList<User>();
		centroid = firstUser;
	}

	public User getCentroid() {
		return centroid;
	}

	public void updateCentroid() {
		
		if(users.size() > 0) {
			double[] featureAverage = new double[users.size()];
			ArrayList<double[]> features = new ArrayList<double[]>();
			
			// Build array of every users features array
			for (User user : users) {
				
				features.add(user.getFeatureRatigns());
			}
			
			// for every user's features array compute the average and
			// store them in featureAverage
			int counter = 0;
			for(double[] array : features) {
				
				double userTotal = 0;
				for(int i = 0; i < array.length; i++) {
					userTotal += array[i];
				}
				
				featureAverage[counter] = userTotal/array.length;
				counter++;
			}
			
			// find the average of every users feature average
			double result = 0d;
			for(int k = 0; k < featureAverage.length; k++) {
				result += featureAverage[k];
			}
			result = result/featureAverage.length;

			// find the user whose feature average is closest to the result
			// and set them as our centroid
			centroid = findAverageUser(featureAverage, result);
		}
		
	}
	
	private User findAverageUser(double[] average, double result) {
		
		double currentMatch = average[0];
		int currentIndex = 0;
		for(int k = 1; k < average.length - 1; k++) {
			double current = average[k];
			
			if(Math.min(Math.abs(current), Math.abs(result)) != currentMatch) {
				currentIndex = k;
				currentMatch = current;
			}
			
		}
		
		return users.get(currentIndex);
	}

	public List<User> getUsers() {
		return users;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("This cluster contains the following users:\n");
		for (User user : users)
			builder.append(user.toString() + ",\n");
		return builder.deleteCharAt(builder.length() - 2).toString(); 
	}
	
	public String toHTML() {
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<div>");

		for (User user : users) {
			htmlBuilder.append(user.getName() + ", ");
		}
		
		htmlBuilder.append("</div>");
		return htmlBuilder.toString();
	}
}
