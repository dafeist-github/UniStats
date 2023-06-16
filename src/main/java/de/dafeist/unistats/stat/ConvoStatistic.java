package de.dafeist.unistats.stat;

import java.util.ArrayList;

import de.dafeist.unistats.stat.trigger.ConvoTrigger;

public class ConvoStatistic {
	
	
	public String name;
	public String description;
	
	public int count = 0;
	
	public static ArrayList<ConvoStatistic> statistics = new ArrayList<ConvoStatistic>();
	
	public ArrayList<ConvoTrigger> triggers = new ArrayList<ConvoTrigger>();
	
	public ConvoStatistic(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public void addTrigger(ConvoTrigger trigger) {
		if(!triggers.contains(trigger)) triggers.add(trigger);
	}
	
	public void count() {
		count++;
	}
	
	public void add(int amount) {
		count += amount;
	}
	
	public void remove(int amount) {
		count -= amount;
	}
	
}
