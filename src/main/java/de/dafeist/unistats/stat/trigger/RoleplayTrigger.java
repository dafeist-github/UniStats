package de.dafeist.unistats.stat.trigger;

public class RoleplayTrigger {
	
	public enum METype {
		SELF,
		OTHER,
		ANY
	}
	
	public METype type = null;
	public String[] include;
	public String[] exclude;
	
	public RoleplayTrigger(METype type, String[] include, String[] exclude) {
		this.type = type;
		this.include = include;
		this.exclude = exclude;
	}
	
	public METype getType() {
		return type;
	}
	
	public RoleplayTrigger(METype type, String[] include) {
		this.type = type;
		this.include = include;
		this.exclude = null;
	}
	
	public RoleplayTrigger(METype type, String include) {
		String[] in = {include};
		this.type = type;
		this.include = in;
		this.exclude = null;
	}
	
}
