package de.dafeist.unistats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import de.dafeist.unistats.stat.CalculatedStatistic;
import de.dafeist.unistats.stat.ConvoStatistic;
import de.dafeist.unistats.stat.RoleplayStatistic;
import de.dafeist.unistats.stat.Statistic;
import de.dafeist.unistats.stat.TimebasedStatistic;

public class DataFinalizer {
	
	public static void run(int linesProcessed, int logsProcessed) {
		
		System.out.println("Finalisiere Daten...");
		
		try {
			
		File file = new File(UniStats.targetFolder.getPath() + "\\" + "data.txt");
		FileWriter writer = new FileWriter(file);
		writer.write("|-------------------------------------------|  \n");
		writer.write("|            UniStats by DaFeist            |  \n");
		writer.write("|               Do not modify               |  \n");
		writer.write("|-------------------------------------------|  \n");
		writer.write("\n");
		
		writer.write("|-------------------------------------------|  \n");
		writer.write("|                  Names:                   |  \n");
		for(int i : UniStats.playerNames.keySet()) {
			writer.write("- " + UniStats.playerNames.get(i) + "\n");
		}
		writer.write("|------------------Player:------------------|  \n");
		
		String finalName = "";
		
		if(UniStats.playerNames.size() > 1) {
		for(int i = 0; i < UniStats.playerNames.size() - 1; i++) {
			String target = UniStats.playerNames.get(i);
			finalName = finalName.replace(target, UniStats.playerNames.get(UniStats.playerNames.size() - 1));
			}
		
		} else {
			finalName = UniStats.playerNames.get(0);
		}
		
		for(String nametrigger : UniStats.aliases.keySet()) {
			if(finalName.contains(nametrigger)) {
				finalName = finalName.replace(nametrigger, UniStats.aliases.get(nametrigger));
			}
		}
		writer.write("- " + finalName + "\n");
		writer.write("|-------------------------------------------|  \n\n");
		
		writer.write("|-------------------------------------------|  \n");
		writer.write("|                Statistics:                |  \n");
		
		for(Statistic statistic : Statistic.statistics) writer.write(statistic.name + ": " + statistic.count + " | Wert: " + statistic.refvalue + "\n");
		writer.write("\n");
		for(RoleplayStatistic statistic : RoleplayStatistic.statistics) writer.write(statistic.name + ": " + statistic.count + "\n");
		writer.write("\n");
		for(ConvoStatistic statistic : ConvoStatistic.statistics) writer.write(statistic.name + ": " + statistic.count + "\n");
		writer.write("\n");
		for(CalculatedStatistic statistic : CalculatedStatistic.statistics) writer.write(statistic.name + ": " + statistic.count + "\n");
		writer.write("\n");
		for(RoleplayStatistic statistic : RoleplayStatistic.hardcoded) writer.write(statistic.name + ": " + statistic.count + "\n");
		writer.write("\n");
		for(TimebasedStatistic statistic : TimebasedStatistic.statistics) {
			int hours = statistic.time / 3600;
			int minutes = (statistic.time % 3600) / 60;
			int seconds = statistic.time % 60;
			String timeString = hours + "h " + minutes + "min " + seconds + "s";
			writer.write(statistic.name + ": " + statistic.count + " / " + timeString + "\n");
		}
		writer.write("|-------------------------------------------|  \n\n|-------------------------------------------|  \n");
		writer.write("|                  Debug:                   |  \n");
		writer.write("Lines processed: " + linesProcessed);
		writer.write("Logs processed: " + logsProcessed);
		writer.write("|-------------------------------------------|  \n\n");
		
		writer.flush();
		
		for(File log : UniStats.targetFolder.listFiles()) {

				BufferedReader reader = new BufferedReader(new FileReader(log));
				
				for(String line; (line = reader.readLine()) != null; ) {
					if(!line.contains("|-------------------------------------------|  ")
							&& !line.contains("|            UniStats by DaFeist            |  ")
							&& !line.contains("|               Do not modify               |  ")
							&& !line.contains("|-------------------------------------------|  ")) {
						writer.write(line);
					}
				}
				writer.write("\n");
			
				writer.flush();
				reader.close();
		}
		
		writer.close();

		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
