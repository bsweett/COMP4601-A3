package edu.carleton.comp4601.assignment3.algorithms;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import edu.carleton.comp4601.assignment3.Main.SocialGraph;
import edu.carleton.comp4601.assignment3.dao.Rule;
import edu.carleton.comp4601.assignment3.dao.Transaction;
import edu.carleton.comp4601.assignment3.util.Tuple;

public class Apriori {
	
	private int support;
	private ConcurrentHashMap<Integer, Transaction>  transactions;
	private ArrayList<Tuple<int[], Integer>> itemSets;
	private ArrayList<Tuple<int[], Integer>> freqItemSets;
	private ArrayList<Rule> rules;
	
	int itemSetSize;
	int transactionCount;
	
	//Initializes Apriori with a given transaction list
	public Apriori(ConcurrentHashMap<Integer, Transaction> transactions) {
		this.transactions = transactions;
		this.transactionCount = transactions.size();
		this.freqItemSets = new ArrayList<Tuple<int[], Integer>>();
	}
	
	//Runs the Apriori algorithm given a support
	//Returns all itemsets that meet the given support
	public ArrayList<Tuple<int[], Integer>> runApriori(int support) throws FileNotFoundException, UnsupportedEncodingException {
		this.support = (int)(transactionCount*(support/100.0f));
		
		initItemSets();
		
		while(itemSets.size() > 0) {
			calculateFrequencies();
			dropItemSets();
			calculateNewItemSets();
		}
		
		generateRules();
		printItemSets();
		
		System.out.println("Apriori is COMPLETE!");
		SocialGraph.getInstance().setA4Ready(true);
		
		return freqItemSets;
	}

	//Finds the unique items across all transactions
	//and build the initial item sets of size 1
	private void initItemSets() {
		
		//Find the unique items across all transactions
		HashSet<Integer> uniqueItemSet = new HashSet<Integer>();
		
		for(Entry<Integer, Transaction> entry: transactions.entrySet()) {
			for(int item: entry.getValue().getItems()) {
				uniqueItemSet.add(item);
			}
		}
		
		ArrayList<Integer> uniqueItems = new ArrayList<Integer>(uniqueItemSet);
		
		//Build the initial item sets of size 1
		itemSets = new ArrayList<Tuple<int[], Integer>>();
		
		for(int item: uniqueItems) {
			int[] itemSet = {item};
			itemSets.add(new Tuple<int[], Integer>(itemSet, 0));
		}
		
		itemSetSize = 1;
	}
	
	//Finds the frequency of each itemset across all transactions
	private void calculateFrequencies() {
		for(Entry<Integer, Transaction> transactionEntry: transactions.entrySet()) {
			ArrayList<Integer> transactionItems = transactionEntry.getValue().getItems();
			int count = 0;
			for(Tuple<int[], Integer> itemSet: itemSets) {
				int[] currentItemSet = itemSet.x;
				int itemMatchCount = 0;
				for(int item: transactionItems) {
					for(int i=0; i<itemSetSize; i++) {
						if(currentItemSet[i] == item) {
							itemMatchCount++;
						}
					}
				}
				if(itemMatchCount == itemSetSize) {
					int newFrequency = itemSet.y + 1;
					itemSets.set(count, new Tuple<int[], Integer>(itemSet.x, newFrequency));
				}
				count++;
			}
		}
	}
	
	//Removes any item sets that are below the support
	private void dropItemSets() {
		ArrayList<Tuple<int[], Integer>> tempItemSets = itemSets;
		itemSets = new ArrayList<Tuple<int[], Integer>>();
		
		for(Tuple<int[], Integer> itemSet: tempItemSets) {
			
			if(itemSet.y >= support) {
				itemSets.add(itemSet);
			} 
		}
		freqItemSets.addAll(itemSets);
	}
	
	//Re-create itemsets based on all unique possibilities of remaining sets
	private void calculateNewItemSets() {
		itemSetSize++;
		HashMap<String, int[]> newItemSets = new HashMap<String, int[]>();
		
		int loopCount = 0;
		for(Tuple<int[], Integer> itemSet: itemSets) {
			//System.out.println(itemSets.size());
			//System.out.println("Current count: " + loopCount);
			loopCount++;
			int currentLoop = 0;
			
			for(Tuple<int[], Integer> nextItemSet: itemSets) {
				if(currentLoop == loopCount) {
					int[] itemSetArray = itemSet.x;
					int[] nextItemSetArray = nextItemSet.x;
					
					int[] newItemSet = new int[itemSetSize];
					
					//Initialize the new item set with values from the current item set
					for(int i=0; i<newItemSet.length - 1; i++) {
						newItemSet[i] = itemSetArray[i];
					}
					
					int numDifferences = 0;
					
					for(int j=0; j<nextItemSetArray.length; j++) {
						
						boolean isFound = false;
						
						for(int i=0; i<itemSetArray.length; i++) {
							if(nextItemSetArray[j] == itemSetArray[i]) {
								isFound = true;
								break;
							}
						}
						
						if(!isFound) {
							numDifferences++;
							newItemSet[newItemSet.length - 1] = nextItemSetArray[j];
						}
					}
					
					if(numDifferences == 1) {
						Arrays.sort(newItemSet);
						newItemSets.put(Arrays.toString(newItemSet), newItemSet);
					}
				} else {
					currentLoop++;
				}
			}
		}
		
		itemSets = new ArrayList<Tuple<int[], Integer>>();
		ArrayList<int[]> newItemsToAdd = new ArrayList<int[]>(newItemSets.values());
		
		for(int[] itemSet: newItemsToAdd) {
			itemSets.add(new Tuple<int[], Integer>(itemSet, 0));
		}
	}
	
	private void generateRules() {
		for(Tuple<int[], Integer> itemSet: freqItemSets) {
			ArrayList<int[]> subsets = getAllSubsets(itemSet.x);
			
			for(int[] setA: subsets) {
				for(int[] setB: subsets) {
					boolean contains = false;
					
					for(int i=0; i<setA.length; i++) {
						for(int j=0; j<setB.length; j++) {
							
							if(setA[i] == setB[j]) {
								contains = true;
								break;
							}
						}
						if(contains) {
							break;
						}
					}
					if(!contains) {
						int confidence = itemSet.y/findSupportOfItemSet(setA);
						
						rules.add(new Rule(setA, setB, confidence));
					}
				}
			}
		}
	}
	
	private ArrayList<int[]> getAllSubsets(int[] freqItemSet) {

        ArrayList<int[]> allsubsets = new ArrayList<int[]>();
        
        //2^n subsets given size of set
        int total = 1 << freqItemSet.length;       

        for (int i = 0; i < total; i++) {
            int[] subset = new int[total];
            for (int j = 0; j < freqItemSet.length; j++) {
                if (((i >> j) & 1) == 1) {
                    subset[j] = freqItemSet[j];
                }
            }
            Arrays.sort(subset);
            allsubsets.add(subset);
        }
        
        return allsubsets;
    }
	
	private int findSupportOfItemSet(int[] itemSet) {
		for(Tuple<int[], Integer> freqItemSet: freqItemSets) {
			if(Arrays.equals(freqItemSet.x, itemSet)) {
				return freqItemSet.y;
			}
		}
		return 0;
	}
	
	private void printItemSets() throws FileNotFoundException, UnsupportedEncodingException {
		
		final String homePath = System.getProperty("user.home");
		PrintWriter writer = new PrintWriter(homePath + "/data/comp4601a3/outputs/apriori.txt", "UTF-8");
		
		writer.println("=============================================================");
		writer.println("   Item sets that support " + (support * 100.0f) / transactionCount + "% of transactions");
		writer.println("=============================================================");
		writer.println("");
		for(Tuple<int[], Integer> itemSet: freqItemSets) {
			int[] itemSetArray = itemSet.x;
			
			writer.print("Item Set: [");
			for(int i=0; i<itemSetArray.length; i++) {
				writer.print(" " + itemSetArray[i] + " ");
			}
			writer.print("] # of transactions: " + itemSet.y);
			writer.println("");
		}
		writer.println("");
		writer.println("=============================================================");
		
		writer.close();
	}
}
