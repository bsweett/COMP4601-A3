package edu.carleton.comp4601.assignment3.util;

import java.util.ArrayList;

public class Utils {

	/**
	 * Takes an ArrayList of integers and returns an array of size 2 containing the top
	 * 2 highest result. A second result is only returned if the secondary score its greater
	 * than 65% of the max score.
	 * 
	 * @param array
	 * @return
	 */
	public static int[] findTwoHighestDistinctValues(ArrayList<Integer> array) {
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
	    
	    if(secondMax > (max * 0.65)) {
	    	return new int[] { array.indexOf(max), array.indexOf(secondMax) };
	    } else {
	    	return new int[] { array.indexOf(max), -1 };
	    }
	}
}