package de.dafeist.unistats.stat;

import java.util.ArrayList;

import de.dafeist.unistats.Action;
import de.dafeist.unistats.stat.trigger.RoleplayTrigger;


public class RoleplayStatistic {
	
	public String name;
	public String description;
	
	public int count = 0;
	
	public static ArrayList<RoleplayStatistic> statistics = new ArrayList<RoleplayStatistic>();
	public static ArrayList<RoleplayStatistic> hardcoded = new ArrayList<RoleplayStatistic>();
	
	public ArrayList<RoleplayTrigger> triggers = new ArrayList<RoleplayTrigger>();
	
	public Action rpAction = null;
	
	//Statistics are bound to the instance
	public RoleplayStatistic(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public RoleplayStatistic(String name, String description, Action rpAction) {
		this.name = name;
		this.description = description;
		this.rpAction = rpAction;
	}
	
	public void addTrigger(RoleplayTrigger trigger) {
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
