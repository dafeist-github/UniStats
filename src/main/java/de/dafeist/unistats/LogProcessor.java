package de.dafeist.unistats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import de.dafeist.unistats.stat.Statistic;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

public class LogProcessor {
	
	public static void process() {
		
		ArrayList<Line> lines = new ArrayList<Line>();
		
		System.out.println("Der Folgende Prozess kann eine lange Zeit dauern");
		System.out.println("Es wird viel CPU-Leistung und möglicherweise Arbeitsspeicher in Anspruch genommen");
		
		int logAmt = UniStats.logFolder.listFiles().length;
		int logsProcessed = 0;
		int linesProcessed = 0;
		
		initStats();
		
		ProgressBar progress = new ProgressBarBuilder().setTaskName("Verarbeite Datens" + "ä" + "tze...")
				.setInitialMax(logAmt)
				.setStyle(ProgressBarStyle.ASCII)
				.continuousUpdate()
				.build();
		
		File targetFolder = UniStats.targetFolder;
		
		for(File file : UniStats.logFolder.listFiles()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				FileWriter writer = new FileWriter(targetFolder.getPath() + "\\data\\" + file.getName());
				
				//To process all the data, we gotta insert all data into an Array
				for(String line; (line = reader.readLine()) != null; ) {
					lines.add(Line.fromString(line));
				}
				
				//Now we can finally process them :)
				for(Line line : lines) {
					analyzeLine(line);
					linesProcessed++;
				}
				
				reader.close();
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			logsProcessed++;
			progress.step();
			
		}
		
		progress.close();
		
	}
	
	public static void initStats() {
		
	}
	
	public static void analyzeLine(Line line) {
		for(Statistic statistic : Statistic.statistics) {
			for(String string : statistic.triggers) {
				if(line.getContent().contains(string)) {
					statistic.count();
					if(statistic.actionTrigger != null) line.setAction(statistic.actionTrigger);
				}
				
				
				
			}
			
		}
		
		
	}
	
}
