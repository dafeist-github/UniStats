package de.dafeist.unistats.stat.trigger;

public class ConvoTrigger {
	
	public enum MSender {
		SELF,
		ANY
	}
	
	public MSender msender = null;
	public String[] include;
	public String[] exclude;
	
	public ConvoTrigger(MSender msender, String[] include, String[] exclude) {
		this.msender = msender;
		this.include = include;
		this.exclude = exclude;
	}
	
	public MSender getMsender() {
		return msender;
	}
	
	public ConvoTrigger(MSender msender, String[] include) {
		this.msender = msender;
		this.include = include;
		this.exclude = null;
	}
	
	public ConvoTrigger(MSender msender, String include) {
		String[] in = {include};
		this.msender = msender;
		this.include = in;
		this.exclude = null;
	}
	
}
