package edu.carleton.comp4601.assignment3.util;

import java.util.HashSet;
import java.util.Set;

import edu.carleton.comp4601.assignment3.dao.Advertisement;

public class AdvertisementFactory {

	private Set<Advertisement> list;
	
	public AdvertisementFactory() {
		this.list = buildAdvertisements();
	}
	
	private Set<Advertisement> buildAdvertisements() {
		
		Category[] allGenres = Category.values();
		
		// All Single Ads
		Set<Advertisement> allAdverts = new HashSet<Advertisement>();
		/*for(Category cat : allGenres) {
			Advertisement ad = new Advertisement(cat, Category.NONE);
			allAdverts.add(ad);
		}*/
		
		// All Pair Ads (None included)
		for (int i = 0; i < allGenres.length -1; i++) {
			for(int j = i + 1; j < allGenres.length; j++) {
				
				/*
				if(j == 14) {
					break;
				}*/
				
				Advertisement adPair = new Advertisement(allGenres[i], allGenres[j]);
				allAdverts.add(adPair);
			}
		}

		return allAdverts;
	}
	
	public Advertisement getAdForCategories(Category cat1, Category cat2) {
		System.out.println(this.getList().size());
		for(Advertisement a : getList()) {
			if(a.getMainGenre() == cat1 && a.getSecondGenre() == cat2) {
				return a;
			}
		}
		
		return null;
	}
	
	public Set<Advertisement> getAdsForCategory(Category cat) {
		Set<Advertisement> results = new HashSet<Advertisement>();
		
		for(Advertisement a : getList()) {
			if(a.getMainGenre() == cat || a.getSecondGenre() == cat) {
				results.add(a);
			}
		}
		
		return results;
	}
	
	public Set<Advertisement> getList() {
		return this.list;
	}
}
