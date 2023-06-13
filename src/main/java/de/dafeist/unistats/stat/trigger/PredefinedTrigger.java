package de.dafeist.unistats.stat.trigger;

public class PredefinedTrigger {
	
	public String before;
	public int multiplier;
	public String after;
	public boolean ph = false;
	
	public PredefinedTrigger(String before, int multiplier, String after) {
		this.before = before;
		this.multiplier = multiplier;
		this.after = after;
	}
	
	public PredefinedTrigger(String before, int multiplier, String after, boolean ph) {
		this.before = before;
		this.multiplier = multiplier;
		this.after = after;
		this.ph = ph;
	}
	
	
	
}
