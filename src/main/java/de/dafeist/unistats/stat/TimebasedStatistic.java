package de.dafeist.unistats.stat;

import java.util.ArrayList;

import de.dafeist.unistats.Action;
import de.dafeist.unistats.stat.trigger.PredefinedTrigger;

public class TimebasedStatistic {
	
	//Time in Seconds
	public int time = 0;
	
	public String s = null;
	
	public int count = 0;

	public String name;
	public String description;
	
	public ArrayList<String> startTriggers = new ArrayList<String>();
	public ArrayList<String> endTriggers = new ArrayList<String>();
	
	public ArrayList<String> startTriggerBlacklist = new ArrayList<String>();
	
	public ArrayList<PredefinedTrigger> predefinedTriggers = new ArrayList<PredefinedTrigger>();
	
	public static ArrayList<TimebasedStatistic> statistics = new ArrayList<TimebasedStatistic>();
	
	public Action startActionTrigger = null;
	public Action endActionTrigger = null;
	
	//Statistics are bound to the instance
	public TimebasedStatistic(String name, String description, Action startActionTrigger, Action endActionTrigger) {
		this.name = name;
		this.description = description;
		this.startActionTrigger = startActionTrigger;
		this.endActionTrigger = endActionTrigger;
	}
	
	public void addStartTriggerBlacklist(String key) {
		if(!startTriggerBlacklist.contains(key)) startTriggerBlacklist.add(key);
	}
	
	public void addStartTrigger(String trigger) {
		if(!startTriggers.contains(trigger)) startTriggers.add(trigger);
	}
	
	public void addEndTrigger(String trigger) {
		if(!endTriggers.contains(trigger)) endTriggers.add(trigger);
	}
	
	public void addPredefinedTrigger(String before, int multiplier, String after, String regex) {
		PredefinedTrigger pt = new PredefinedTrigger(before, multiplier, after, regex);
		if(!predefinedTriggers.contains(pt)) predefinedTriggers.add(pt);
	}
	
	public int getTime() {
		return time;
	}
	
	public int inMinutes() {
		return time / 60;
	}
	
	public int inHours() {
		return time / 3600;
	}
	
	public void add(int amount) {
		time += amount;
		count++;
	}
	
	public void remove(int amount) {
		time -= amount;
	}

}
