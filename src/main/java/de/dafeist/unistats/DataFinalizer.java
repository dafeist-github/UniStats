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
