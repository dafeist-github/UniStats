package de.dafeist.unistats.stat.trigger;

public class PredefinedTrigger {
	
	public String before;
	public int multiplier;
	public String after;
	
	public PredefinedTrigger(String before, int multiplier, String after) {
		this.before = before;
		this.multiplier = multiplier;
		this.after = after;
	}
	
}
