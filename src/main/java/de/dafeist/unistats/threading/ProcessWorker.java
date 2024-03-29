package de.dafeist.unistats.threading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.dafeist.unistats.Action;
import de.dafeist.unistats.UniStats;
import de.dafeist.unistats.crypto.Hashing;
import de.dafeist.unistats.parse.StringUtils;

public class ProcessWorker implements Runnable {
	
	private final Map<String, List<File>> queue;
	
	public ProcessWorker(Map<String, List<File>> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		
		for(Entry<String, List<File>> bucket : queue.entrySet()) {
		File prev = null;
		File target = null;
		
		for(File file : bucket.getValue()) {
			
			int appends = 1;
			
			boolean headerLock = false;
			
			FileWriter writer = null;
			
			boolean hasData = false;
			boolean append = false;
			boolean onUC = false;
			boolean prevWasUC = false;
			
			if(queue.size() > 1 && prev != null) {
				target = prev;
				
				if(target.length() > 1) {
					headerLock = true;
				} else {
					headerLock = false;
				}
				
				append = true;
				onUC = false;
				
				try {
					writer = new FileWriter(target, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else { 
				headerLock = false;
				appends = 1;
				
				target = new File(UniStats.logFolder + "\\" + StringUtils.rmLastChar(file.getName().replace(file.getName().split("-")[3], "")) + ".txt");
				
				try {
					writer = new FileWriter(target);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			try {
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
		    for(String line; (line = reader.readLine()) != null; ) {
		    	
		    	UniStats.linesProcessed++;
		    	
		    	//Check for username
		    	if(line.contains("[main/INFO]: Setting user: ")) {
		    		line = line.split(" ")[4];
					//Write FileHeader
		    		if(!headerLock) {
					for(String string : UniStats.generateLogHeader(line, String.valueOf(UniStats.logsProcessed), Hashing.sha256(file.getPath()), StringUtils.parseNumDateFromLog(file.getName()))) {
						writer.write(string);
					}
					writer.write("\n");
					headerLock = true;
		    		}
					if(UniStats.playerNames.size() == 0) UniStats.playerNames.put(0, line);
					if(!UniStats.playerNames.get(UniStats.playerNames.size() - 1).equalsIgnoreCase(line)) UniStats.playerNames.put(UniStats.playerNames.size(), line);
		    	} else if(line.contains("[Client thread/INFO]: Setting user: ")) {
		    		line = line.split(" ")[5];
					//Write FileHeader
		    		if(!headerLock) {
					for(String string : UniStats.generateLogHeader(line, String.valueOf(UniStats.logsProcessed), Hashing.sha256(file.getPath()), StringUtils.parseNumDateFromLog(file.getName()))) {
						writer.write(string);
					}
					writer.write("\n");
					headerLock = true;
		    		}
					if(UniStats.playerNames.size() == 0) UniStats.playerNames.put(0, line);
					if(!UniStats.playerNames.get(UniStats.playerNames.size() - 1).equalsIgnoreCase(line)) UniStats.playerNames.put(UniStats.playerNames.size(), line);
		    	} else if(line.contains("[Client thread/INFO] [net.minecraft.client.Minecraft]: Setting user: ")) {
		    		line = line.split(" ")[6];
					//Write FileHeader
		    		if(!headerLock) {
					for(String string : UniStats.generateLogHeader(line, String.valueOf(UniStats.logsProcessed), Hashing.sha256(file.getPath()), StringUtils.parseNumDateFromLog(file.getName()))) {
						writer.write(string);
					}
					writer.write("\n");
					headerLock = true;
		    		}
					if(UniStats.playerNames.size() == 0) UniStats.playerNames.put(0, line);
					if(!UniStats.playerNames.get(UniStats.playerNames.size() - 1).equalsIgnoreCase(line)) UniStats.playerNames.put(UniStats.playerNames.size(), line);
		    	}
		    	
		    	//Listen to Server
		    	if(line.contains("[Client thread/INFO] [net.minecraft.client.multiplayer.GuiConnecting]: Connecting to ")) {
		    		for(String domain : UniStats.targetServers) {
		    			if(line.split(" ")[6].replace(",", "").equalsIgnoreCase(domain)) {
		    				onUC = true;
		    				if(domain.endsWith(".")) domain =  domain.substring(0, domain.length() - 1);
		    				writer.write(UniStats.format(line.split(" ")[0], Action.CONNECT, "Connected to Server ") + domain + " \n");
		    				prevWasUC = true;
		    				break;
		    			} else {
		    				onUC = false;
		    			}
		    			
		    		}
		    		if(!onUC && prevWasUC == true) {
		    			writer.write(UniStats.format(line.split(" ")[0], Action.DISCONNECT, "Disconnected from Server" + " \n" ));
		    			prevWasUC = false;
		    		}
		    	} else if(line.contains("[main/INFO]: Connecting to ")) {
		    		for(String domain : UniStats.targetServers) {
		    			if(line.split(" ")[4].replace(",", "").equalsIgnoreCase(domain)) {
		    				onUC = true;
		    				if(domain.endsWith(".")) domain =  domain.substring(0, domain.length() - 1);
		    				writer.write(UniStats.format(line.split(" ")[0], Action.CONNECT, "Connected to Server ") + domain + " \n");
		    				prevWasUC = true;
		    				break;
		    			} else {
		    				onUC = false;
		    			}
		    			
		    		}
		    		if(!onUC && prevWasUC == true) {
		    			writer.write(UniStats.format(line.split(" ")[0], Action.DISCONNECT, "Disconnected from Server" + " \n" ));
		    			prevWasUC = false;
		    		}
		    	}
		    	
		    	if(!onUC) continue;
		    	
		    	if(line.contains("[CHAT]")) {
		    		hasData = true;
		    		writer.write(UniStats.format(line.split(" ")[0], Action.CHAT, line.substring(line.indexOf("[CHAT] ") + 7)) + " \n");
		    	}
		        
		    }
		    
			if(append && hasData) {
				try {
					
				appends++;
					
				writer.flush();
				writer.close();
				FileWriter appendDct = new FileWriter(target, true);
				
				appendDct.write("\n[UniStats] Detected new Session-Start, total: " + appends + " \n");
					
				appendDct.flush();
				appendDct.close();
				
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			} else {
				writer.flush();
				writer.close();
			}
			
			reader.close();
			
			//Kill log if empty
			if(!append && !hasData || target.length() < 460) {
				target.delete();
			}
			
			prev = target;
			
		    } catch(Exception e) {
		    	e.printStackTrace();
		    }
			
			UniStats.logsProcessed++;
			UniStats.progress.step();
			
		}
		}
		
	}

}
