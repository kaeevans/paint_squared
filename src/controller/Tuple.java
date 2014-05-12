package controller;

/** Represents a Tuple (ie: (x,y) ) object */
public class Tuple {
	public String name = "";
	public int number = 0;
	
	/** @param token The token to create a tuple from */
	public Tuple(String token){
		String[] tokens = token.split("`");
		if(tokens.length == 2) {
			name = tokens[0];
			number = Integer.parseInt(tokens[1]);
		} else
			System.err.println("Warning, failed parse attempt of "+token);
	}
	
	/**
	 * @param n The first parameter in the tuple
	 * @param num The second parameter in the tuple
	 */
	public Tuple(String n, int num) {
		name = n;
		number = num;
	}
	
	public boolean equals(Object o) {
	    if (!(o instanceof Tuple)) 
	        return false;
	    Tuple other = (Tuple) o;
	    return this.name.equals(other.name) && this.number == other.number;
	    
	}
}
