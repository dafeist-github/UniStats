package de.dafeist.unistats.stat.trigger;

public class RoleplayTrigger {
	
	public enum METype {
		SELF,
		OTHER
	}
	
	public METype type = null;
	String[] include;
	String[] exclude;
	
	public RoleplayTrigger(METype type, String[] include, String[] exclude) {
		this.type = type;
		this.include = include;
		this.exclude = exclude;
	}
	
	public METype getType() {
		return type;
	}
	
	public RoleplayTrigger(METype type, String[] include) {
		new RoleplayTrigger(type, include, null);
	}
	
}
