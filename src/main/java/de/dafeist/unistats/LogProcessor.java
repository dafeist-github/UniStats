package de.dafeist.unistats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

public class LogProcessor {
	
	public static void process() {
		
		System.out.println("Der Folgende Prozess kann eine lange Zeit dauern");
		System.out.println("Es wird viel CPU-Leistung und möglicherweise Arbeitsspeicher in Anspruch genommen");
		
		int logAmt = UniStats.logFolder.listFiles().length;
		
		ProgressBar progress = new ProgressBarBuilder().setTaskName("Verarbeite Datens" + "ä" + "tze...")
				.setInitialMax(logAmt)
				.setStyle(ProgressBarStyle.ASCII)
				.continuousUpdate()
				.build();
		
		File targetFolder = UniStats.targetFolder;
		
		for(File file : targetFolder.listFiles()) {
			try {
				FileReader reader = new FileReader(file);
				
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void analyzeLine(String line) {
		
	}
	
}
