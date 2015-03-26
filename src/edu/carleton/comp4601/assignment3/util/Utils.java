package edu.carleton.comp4601.assignment3.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Utils {

	/**
	 * Takes an ArrayList of integers and returns an array of size 2 containing the top
	 * 2 highest result. A second result is only returned if the secondary score its greater
	 * than 65% of the max score.
	 * 
	 * @param array
	 * @return
	 */
	public static int[] findTopTwoGenresForPages(ArrayList<Integer> array) {
	    int max = Integer.MIN_VALUE;
	    int secondMax = Integer.MIN_VALUE;

	    for (int value : array) {
	        if (value > max) {
	            secondMax = max;
	            max = value;
	        } else if (value > secondMax && value < max) {
	            secondMax = value;
	        }
	    }
	    
	    // If the top hit is action, look for another category with greater than 10% of action
	    // results. Make it the main category
	    if( array.indexOf(max) == 0 ) {
	    	if ( secondMax > (max * 0.10)) {
	    		return new int[] { array.indexOf(secondMax), array.indexOf(max) };
	    	}
	    }
	    
	    if(secondMax > (max * 0.65)) {
	    	return new int[] { array.indexOf(max), array.indexOf(secondMax) };
	    } else {
	    	return new int[] { array.indexOf(max), -1 };
	    }
	}
	
	//Finds the top two genres for a cluster
	public static int[] findTopTwoGenresForCluster(ArrayList<Double> array) {
	    int max = Integer.MIN_VALUE;
	    int secondMax = Integer.MIN_VALUE;

	    for (Double value : array) {
	        if (value.intValue() > max) {
	            secondMax = max;
	            max = value.intValue();
	        } else if (value.intValue() > secondMax && value.intValue() < max) {
	            secondMax = value.intValue();
	        }
	    }
	    
	    System.out.println("Max :" + max + " Min: " + secondMax);
	    
	    return new int[] { array.indexOf(((double)max)), array.indexOf(((double)secondMax)) };
	}
	
	//Converts a string delimited by | into an array
	public static int[] stringToIntArray(String arr) {
		String[] items = arr.split("\\|");

		int[] results = new int[items.length];

		for (int i = 0; i < items.length; i++) {
		    try {
		        results[i] = Integer.parseInt(items[i]);
		    } catch (NumberFormatException nfe) {};
		}
		
		return results;
	}
	
	//Convert from integer array to set if integers
	public static Set<Integer> intArrayToSet(int[] array) {
		Set<Integer> set = new HashSet<Integer>();
		
		for(int i=0; i<array.length; i++) {
			set.add(array[i]);
		}
		
		return set;
	}
	
	//Convert from setof integers to integer array
	public static int[] setToIntArray(Set<Integer> set) {
		int[] array = new int[set.size()];
		int count = 0;
		for(int i: set) {
			array[count] = i;
			count++;
		}
		return array;
	}
}