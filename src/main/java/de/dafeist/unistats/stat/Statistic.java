package de.dafeist.unistats.stat;

import java.util.ArrayList;

import de.dafeist.unistats.Action;
import de.dafeist.unistats.stat.trigger.PredefinedTrigger;

public class Statistic {
	
	public String name;
	public String description;
	
	public int count = 0;
	public int refvalue = 0;
	
	public ArrayList<String> triggers = new ArrayList<String>();
	
	public ArrayList<PredefinedTrigger> predefinedTriggers = new ArrayList<PredefinedTrigger>();
	
	public static ArrayList<Statistic> statistics = new ArrayList<Statistic>();
	
	public Action actionTrigger = null;
	
	//Statistics are bound to the instance
	public Statistic(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public Statistic(String name, String description, Action actionTrigger) {
		this.name = name;
		this.description = description;
		this.actionTrigger = actionTrigger;
	}
	
	public void addPredefinedTrigger(String before, int multiplier, String after) {
		PredefinedTrigger pt = new PredefinedTrigger(before, multiplier, after);
		if(!predefinedTriggers.contains(pt)) predefinedTriggers.add(pt);
	}
	
	public void addPredefinedTrigger(String before, int multiplier, String after, boolean ph) {
		PredefinedTrigger pt = new PredefinedTrigger(before, multiplier, after, ph);
		if(!predefinedTriggers.contains(pt)) predefinedTriggers.add(pt);
	}
	
	public void addTrigger(String trigger) {
		if(!triggers.contains(trigger)) triggers.add(trigger);
	}
	
	public void count() {
		count++;
	}
	
	public void addValue(int amount) {
		refvalue += amount;
	}
	
	public void removeValue(int amount) {
		refvalue -= amount;
	}
	
	public void add(int amount) {
		count += amount;
	}
	
	public void remove(int amount) {
		count -= amount;
	}
	
	
}
