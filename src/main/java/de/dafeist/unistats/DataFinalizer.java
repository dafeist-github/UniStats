package de.dafeist.unistats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

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
		
		List<String> alr = new ArrayList<String>();
		
		for(int i : UniStats.playerNames.keySet()) {
			String name = UniStats.playerNames.get(i);
			
			if(alr.contains(name)) continue;
			
			writer.write("- " + name + "\n");
			alr.add(name);
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
		writer.write("|-------------------------------------------|  \n");
		writer.write("\n");
		
		writer.write("|-------------------------------------------|  \n");
		writer.write("|                Statistics:                |  \n");
		
		for(Statistic statistic : Statistic.statistics) {
			if(statistic.refvalue >= 1) {
				writer.write(statistic.name + ": " + statistic.count + " | Wert: " + statistic.refvalue + "\n");
			} else {
				writer.write(statistic.name + ": " + statistic.count + "\n");
			}
		}
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
		writer.write("|-------------------------------------------|  \n");
	    writer.write("\n|-------------------------------------------|  \n");
		writer.write("|                  Debug:                   |  \n");
		writer.write("Lines processed: " + linesProcessed + "\n");
		writer.write("Logs processed: " + logsProcessed + "\n");
		writer.write("|-------------------------------------------|  \n");
		writer.write("\n");
		
		for(File log : UniStats.ddFolder.listFiles()) {
				if(log.isDirectory()) continue;
				BufferedReader reader = new BufferedReader(new FileReader(log));
				
				for(String line; (line = reader.readLine()) != null; ) {
					if(!line.contains("|-------------------------------------------|  ")
							&& !line.contains("|            UniStats by DaFeist            |  ")
							&& !line.contains("|               Do not modify               |  ")
							&& !line.contains("|-------------------------------------------|  ")) {
						writer.write(line + "\n");
					}
				}
				writer.write("\n");
			
				writer.flush();
				reader.close();
		}
		
		writer.close();
		
		File compressed = new File(UniStats.targetFolder.getPath() + File.separator + "data.gz");
		compressed.createNewFile();
		
		UniStats.compressGzip(file, compressed);

		System.out.println("Fertig! Die Datei data.txt im Ordner 'unidata -> target' zeigt alle deine Statistiken");

		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
