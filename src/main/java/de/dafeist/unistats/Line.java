package de.dafeist.unistats;

public class Line {
	

	String time;
	Action action;
	String content;
	
	public Line(String time, Action action, String content) {
		this.time = time;
		this.action = action;
		this.content = content;
	}
	
	public static Line fromString(String line) {
		String time = line.split(" ")[0].replace("[", "").replace("]", "");
		Action action = Action.valueOf(line.split(" ")[1].replace("[", "").replace("]", ""));
		
		if(action == null) action = Action.CHAT;
		
		String content = line.substring(line.indexOf(line.split(" ")[1].replace("[", "").replace("]", "")) + action.toString().length() + 2);
		return new Line(time, action, content);
	}
	
	@Override
	public String toString() {
		if(action == Action.CHAT) action = null;
		if(action == Action.CONNECT) return "[" + time + "] [CONNECT] " + content;
		if(action == Action.DISCONNECT) return "[" + time + "] [DISCONNECT] " + content;
		if(action == null) return "[" + time + "] [CHAT] " + content;
		return "[" + time + "] [CHAT] " + content + " ".repeat(500) + " | " + action.toString();
	}
	
	public static int timeInSeconds(String time) {
		String[] num = time.split(":");
		return Integer.parseInt(num[0])*3600+Integer.parseInt(num[1])*60+Integer.parseInt(num[2]);
	}
	
	public static int timeDiff(String start, String end) {
		return timeInSeconds(end)-timeInSeconds(start);
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Action getAction() {
		return action;
	}
	

	public void setAction(Action action) {
		this.action = action;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
