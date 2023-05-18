package de.dafeist.unistats;

import de.dafeist.unistats.UniStats.Action;

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
		Action action = Action.valueOf(time.split(" ")[1].replace("[", "").replace("]", ""));
		String content = line.substring(line.indexOf(time.split(" ")[1].replace("[", "").replace("]", "")) + action.toString().length() + 2);
		return new Line(time, action, content);
	}
	
	@Override
	public String toString() {
		return "[" + time + "] [" + action.toString() + "] " + content;
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
