package de.dafeist.unistats.stat;

import java.util.ArrayList;

public class CalculatedStatistic {
	
	public String name;
	public String description;
	
	public int count = 0;
	
	public static ArrayList<CalculatedStatistic> statistics = new ArrayList<CalculatedStatistic>();
	
	public ArrayList<Statistic> relNormal = new ArrayList<Statistic>();
	public ArrayList<RoleplayStatistic> relRP = new ArrayList<RoleplayStatistic>();
	public ArrayList<TimebasedStatistic> relTB = new ArrayList<TimebasedStatistic>();
	
	public CalculatedStatistic(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public void add(Statistic statistic) {
		relNormal.add(statistic);
	}
	
	public void add(RoleplayStatistic statistic) {
		relRP.add(statistic);
	}
	
	public void add(TimebasedStatistic statistic) {
		relTB.add(statistic);
	}
	
	public void calc() {
		for(Statistic statistic : relNormal) count = count + statistic.count;
		for(RoleplayStatistic statistic : relRP) count = count + statistic.count;
		for(TimebasedStatistic statistic : relTB) count = count + statistic.count;
	}
	
	
	
}
