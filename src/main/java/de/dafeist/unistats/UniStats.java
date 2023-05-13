package de.dafeist.unistats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import de.dafeist.unistats.crypto.Hashing;
import de.dafeist.unistats.parse.StringUtils;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

public class UniStats {
	
	public static File dataFolder;
	public static File decompFolder;
	public static File logFolder;
	public static File targetFolder;
	
	public static int logAmt = 0;
	
	public static HashMap<Integer, String> playerNames = new HashMap<Integer, String>();
	
	public static String[] targetServers = {"unicacity.de", "unicacity.de.", "tcpshield.unicacity.de", "tcpshield.unicacity.de.", "mc.unicacity.de", "mc.unicacity.de."};
	
	public static File instanceFolder = new File("C:\\Users\\Feist\\Downloads\\testinst");
	
	public static void main(String[] args) {
		initEnvironment();
		
		extractLogs();
		
		processLogs();
	}
	
	public enum Action {
		CONNECT,
		DISCONNECT,
		CHAT
	}
	
	public static void processLogs() {
		System.out.println("Vorbereitung l�uft...");
		logAmt = decompFolder.listFiles().length;
		System.out.println("Dieser Prozess kann je nach CPU und Datenmenge lange dauern");
		
		ProgressBar progress = new ProgressBarBuilder().setTaskName("Bearbeite Datens" + "�" + "tze...")
				.setInitialMax(logAmt)
				.setStyle(ProgressBarStyle.ASCII)
				.continuousUpdate()
				.build();
		
		int logsProcessed = 0;
		int linesProcessed = 0;
		
		for(File file : decompFolder.listFiles()) {
			File target = new File(logFolder + "\\" + file.getName());
			
			try {
			FileWriter writer = new FileWriter(target);
			
			boolean onUC = false;
			boolean headerLock = false;
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			    for(String line; (line = reader.readLine()) != null; ) {
			    	
			    	//Check for username
			    	if(line.contains("[main/INFO]: Setting user: ")) {
			    		System.out.println("sUser");
			    		line = line.split(" ")[4];
						//Write FileHeader
			    		if(!headerLock) {
						for(String string : generateLogHeader(line, String.valueOf(logsProcessed), Hashing.sha256(file.getPath()), StringUtils.parseNumDateFromLog(file.getName()))) {
							writer.write(string);
						}
						writer.write("\n");
						headerLock = true;
			    		}
						if(playerNames.size() == 0) playerNames.put(0, line);
						if(!playerNames.get(playerNames.size() - 1).equalsIgnoreCase(line)) playerNames.put(playerNames.size(), line);
			    	} else if(line.contains("[Client thread/INFO]: Setting user: ")) {
			    		System.out.println("sUser");
			    		line = line.split(" ")[5];
						//Write FileHeader
			    		if(!headerLock) {
						for(String string : generateLogHeader(line, String.valueOf(logsProcessed), Hashing.sha256(file.getPath()), StringUtils.parseNumDateFromLog(file.getName()))) {
							writer.write(string);
						}
						writer.write("\n");
						headerLock = true;
			    		}
						if(playerNames.size() == 0) playerNames.put(0, line);
						if(!playerNames.get(playerNames.size() - 1).equalsIgnoreCase(line)) playerNames.put(playerNames.size(), line);
			    	} else if(line.contains("[Client thread/INFO] [net.minecraft.client.Minecraft]: Setting user: ")) {
			    		System.out.println("sUser");
			    		line = line.split(" ")[6];
						//Write FileHeader
			    		if(!headerLock) {
						for(String string : generateLogHeader(line, String.valueOf(logsProcessed), Hashing.sha256(file.getPath()), StringUtils.parseNumDateFromLog(file.getName()))) {
							writer.write(string);
						}
						writer.write("\n");
						headerLock = true;
			    		}
						if(playerNames.size() == 0) playerNames.put(0, line);
						if(!playerNames.get(playerNames.size() - 1).equalsIgnoreCase(line)) playerNames.put(playerNames.size(), line);
			    	}
			    	
			    	//Listen to Server
			    	if(line.contains("[Client thread/INFO] [net.minecraft.client.multiplayer.GuiConnecting]: Connecting to ")) {
			    		for(String domain : targetServers) {
			    			if(line.split(" ")[6].replace(",", "").equalsIgnoreCase(domain)) {
			    				onUC = true;
			    				if(domain.endsWith(".")) domain =  domain.substring(0, domain.length() - 1);
			    				writer.write(format(line.split(" ")[0], Action.CONNECT, "Connected to Server ") + domain + " \n");
			    				break;
			    			} else {
			    				onUC = false;
			    			}
			    			
			    		}
			    		if(!onUC) writer.write(format(line.split(" ")[0], Action.DISCONNECT, "Disconnected from Server" + " \n" ));
			    	} else if(line.contains("[main/INFO]: Connecting to ")) {
			    		for(String domain : targetServers) {
			    			if(line.split(" ")[4].replace(",", "").equalsIgnoreCase(domain)) {
			    				onUC = true;
			    				if(domain.endsWith(".")) domain =  domain.substring(0, domain.length() - 1);
			    				writer.write(format(line.split(" ")[0], Action.CONNECT, "Connected to Server ") + domain + " \n");
			    				break;
			    			} else {
			    				onUC = false;
			    			}
			    			
			    		}
			    		if(!onUC) writer.write(format(line.split(" ")[0], Action.DISCONNECT, "Disconnected from Server" + " \n" ));
			    	}
			    	
			    	if(!onUC) continue;
			    	
			    	if(line.contains("[CHAT]")) System.out.println("CHAT"); writer.write(format(line.split(" ")[0], Action.CHAT, line.substring(line.indexOf("[CHAT] ") + 1)) + " \n");
			        
			    	linesProcessed++;
			    }
			    
		logsProcessed++;
		progress.step();
		
		writer.flush();
		writer.close();
		reader.close();
		progress.close();
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
			}
	}
	
	public static String format(String time, Action action, String text) {
		return time + " [" + action.toString() + "] " + text;
	}
	
	//playerName is related to date, cannot get uuid
	public static ArrayList<String> generateLogHeader(String playerName, String logID, String origin, String date) {
		ArrayList<String> header = new ArrayList<String>();
		
		String h1 = "|-------------------------------------------| \n";
		String h2 = "|            UniStats by DaFeist            | \n";
		String h3 = "|               Do not modify               | \n";
		String h4 = "|-------------------------------------------| \n";
		String h5 = "\n";
		String h6 = "|---------------------------------------------------------------| \n";
		String h7 = "|Player: " + playerName + "\n";
		String h8 = "|ID: " + logID + "\n";
		String h9 = "|Origin: " + origin + "\n";
	   String h10 = "|Date: " + date + "\n";
	   String h11 = "|---------------------------------------------------------------| \n \n";
		
		header.add(h1);
		header.add(h2);
		header.add(h3);
		header.add(h4);
		header.add(h5);
		header.add(h6);
		header.add(h7);
		header.add(h8);
		header.add(h9);
		header.add(h10);
		header.add(h11);
		
		return header;
	}
	
	public static void initEnvironment() {
		String jarPath = "";
		
        try {
			jarPath = UniStats.class
			    .getProtectionDomain()
			    .getCodeSource()
			    .getLocation()
			    .toURI()
			    .getPath();
			jarPath = new File(jarPath).getParentFile().getPath();
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        
        dataFolder = new File(jarPath + "\\unidata");
        decompFolder = new File(dataFolder.getPath() + "\\decomp");
        logFolder = new File(dataFolder.getPath() + "\\logprocess");
        targetFolder = new File(dataFolder.getPath() + "\\target");
        
		if(!dataFolder.exists()) dataFolder.mkdir();
		if(!decompFolder.exists()) decompFolder.mkdir();
		if(!logFolder.exists()) logFolder.mkdir();
		if(!targetFolder.exists()) targetFolder.mkdir();
	}
	
	public static void extractLogs() {
		ProgressBar extractProgress = new ProgressBarBuilder().setTaskName("Extrahiere logs...")
				.setInitialMax(new File(instanceFolder.getPath() + "\\logs").listFiles().length)
				.setStyle(ProgressBarStyle.ASCII)
				.hideETA()
				.continuousUpdate()
				.build();
		
		for(File file : new File(instanceFolder.getAbsolutePath() + "\\logs").listFiles()) {
			if(file.getName().contains("log.gz")) extractGZip(file);
			extractProgress.step();
		}
		
		extractProgress.close();
	}
	
	public static void extractGZip(File file) {
	    byte[] buffer = new byte[1024];

	    try{

	        GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file.getAbsolutePath()));
	        FileOutputStream out = new FileOutputStream(decompFolder + "\\" + file.getName() + ".txt");

	        int len;
	        while ((len = gzis.read(buffer)) > 0) {
	            out.write(buffer, 0, len);
	        }

	        gzis.close();
	        out.close();

	    } catch(IOException ex){
	        ex.printStackTrace();
	    }
		
	}

}
