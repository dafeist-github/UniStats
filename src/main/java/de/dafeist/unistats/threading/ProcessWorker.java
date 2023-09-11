package de.dafeist.unistats.threading;

import java.io.File;
import java.util.List;

public class ProcessWorker implements Runnable {
	
	private final List<File> queue;
	
	public ProcessWorker(List<File> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		
		for(File file : queue) {
			
		}
		
	}

}
