package edu.carleton.comp4601.assignment3.util;

public enum Category {

	ACTION("Action", 0),
	ADVENTURE("Adventure", 1),
	FAMILY("Family", 2),
	COMEDY("Comedy", 3),
	DOCUMENTARY("Documentary", 4),
	DRAMA("Drama", 5),
	MUSICAL("Musical", 6),
	HORROR("Horror", 7),
	ROMANCE("Romance", 8),
	SCI_FI("Sci-Fi", 9),
	FANTASY("Fantasy", 10),
	THRILLER("Thriller", 11),
	MYSTERY("Mystery", 12),
	NONE("None", -1);
	
	private final String type; 
	private final int id;
	public static final int rateableLength = Category.values().length - 1;
	
	Category(String type, int id) {
		this.type = type;
		this.id = id;
	}
	
	public static Category fromInteger(int x) {
        switch(x) {
        case 0:
            return ACTION;
        case 1:
            return ADVENTURE;
        case 2:
        	return FAMILY;
        case 3:
        	return COMEDY;
        case 4: 
        	return DOCUMENTARY;
        case 5:
        	return DRAMA;
        case 6:
        	return MUSICAL;
        case 7:
        	return HORROR;
        case 8:
        	return ROMANCE;
        case 9:
        	return SCI_FI;
        case 10:
        	return FANTASY;
        case 11:
        	return THRILLER;
        case 12:
        	return MYSTERY;
        case -1:
        	return NONE;
        }
        return null;
    }

    public boolean equalsType(String othertype) {
        return (othertype == null)? false:type.equals(othertype);
    }
    
    public boolean equalsId(int otherId) {
        return (otherId < 0 || otherId > 12)? false:id == otherId;
    }

    public String toString(){
       return type;
    }
    
    public int getId() {
    	return this.id;
    }
}
