package edu.carleton.comp4601.assignment3.algorithms;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.carleton.comp4601.assignment3.Main.SocialGraph;
import edu.carleton.comp4601.assignment3.dao.Rule;
import edu.carleton.comp4601.assignment3.dao.Transaction;
import edu.carleton.comp4601.assignment3.util.Tuple;
import edu.carleton.comp4601.assignment3.util.Utils;

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
		this.rules = new ArrayList<Rule>();
	}

	//Runs the Apriori algorithm given a support
	//Finds a set of frequent item sets and uses this set to generate rules
	//Rules are stored in the social graph class with confidence levels
	public ArrayList<Tuple<int[], Integer>> runApriori(int support) throws FileNotFoundException, UnsupportedEncodingException {
		this.support = (int)(transactionCount*(support/100.0f));

		initItemSets();

		while(itemSets.size() > 0) {
			calculateFrequencies();
			dropItemSets();
			calculateNewItemSets();
		}

		generateRules();

		
		SocialGraph.getInstance().setRules(rules);
		SocialGraph.getInstance().setA4RulesReady(true);
		
		System.out.println("Apriori is COMPLETE!");
		
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

				//Look through all items in the transaction and
				//all items in the item set to see if the itemset
				//is a subset of the transaction set
				for(int item: transactionItems) {
					for(int i=0; i<itemSetSize; i++) {
						if(currentItemSet[i] == item) {
							itemMatchCount++;
						}
					}
				}

				//If transaction set contains as many items as total
				//count of item set then it is a subset
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

			//Only add the itemset if it meets the support
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

					//Initialize the new item set with all values of previous
					//plus one empty value to add to
					for(int i=0; i<newItemSet.length - 1; i++) {
						newItemSet[i] = itemSetArray[i];
					}

					int numDifferences = 0;

					//Find a new set with the combination of a unique value of 
					//the next set and adding it to the current set
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

					//Make sure only 1 new set was made and ensure no duplicates using
					//a key based on the itemset (if duplicate will overwrite)
					if(numDifferences == 1) {
						Arrays.sort(newItemSet);
						newItemSets.put(Arrays.toString(newItemSet), newItemSet);
					}
				} else {
					currentLoop++;
				}
			}
		}

		//Create a new item set with sizes +1 from last iteration
		itemSets = new ArrayList<Tuple<int[], Integer>>();
		ArrayList<int[]> newItemsToAdd = new ArrayList<int[]>(newItemSets.values());

		for(int[] itemSet: newItemsToAdd) {
			itemSets.add(new Tuple<int[], Integer>(itemSet, 0));
		}
	}

	private void generateRules() {
		HashMap<String, Rule> tempRules = new HashMap<String, Rule>();

		for(Tuple<int[], Integer> itemSet: freqItemSets) {
			//Find all the subsets of the frequent item set
			Set<Set<Integer>> setOfSubsets = new HashSet<Set<Integer>>();
			setOfSubsets = powerSet(Utils.intArrayToSet(itemSet.x));

			ArrayList<int[]> subsets = new ArrayList<int[]>();

			for(Set<Integer> set: setOfSubsets) {
				if(set.size() > 0 && set.size() != itemSet.x.length) {
					subsets.add(Utils.setToIntArray(set));
				}
			}

			//For all the subsets of the given frequent item set
			//find all the combinations that do not intersect
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
					//Find the confidence of the rule generated by the subsets
					if(!contains) {
						float numerator = itemSet.y;
						float denominator = findSupportOfItemSet(setA);
						float result = numerator/denominator;
						tempRules.put(Arrays.toString(setA) + Arrays.toString(setB), new Rule(setA, setB, Math.round(result * 100.0f)));
					}
				}
			}
		}

		//Make sure no duplicate rules exist
		rules.addAll(new ArrayList<Rule>(tempRules.values()));
	}

	//Find all subsets of a given set
	public static Set<Set<Integer>> powerSet(Set<Integer> originalSet) {
		Set<Set<Integer>> sets = new HashSet<Set<Integer>>();
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<Integer>());
			return sets;
		}
		List<Integer> list = new ArrayList<Integer>(originalSet);
		Integer head = list.get(0);
		Set<Integer> rest = new HashSet<Integer>(list.subList(1, list.size()));
		for (Set<Integer> set : powerSet(rest)) {
			Set<Integer> newSet = new HashSet<Integer>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
	}

	//Find the support of an item set
	private int findSupportOfItemSet(int[] itemSet) {
		for(Tuple<int[], Integer> freqItemSet: freqItemSets) {
			if(Arrays.equals(freqItemSet.x, itemSet)) {
				return freqItemSet.y;
			}
		}
		return 0;
	}
}
