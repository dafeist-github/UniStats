package de.dafeist.unistats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class DataFinalizer {
	
	public static void run() {
		
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
		writer.write("|-------------------------------------------|  \n");
		
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
