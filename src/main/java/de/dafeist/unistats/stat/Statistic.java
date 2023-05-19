package de.dafeist.unistats.stat;

import java.util.ArrayList;

import de.dafeist.unistats.UniStats.Action;

public class Statistic {
	
	public String name;
	public String description;
	
	public int count = 0;
	
	public ArrayList<String> triggers = new ArrayList<String>();
	
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
	
	public void addTrigger(String trigger) {
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
