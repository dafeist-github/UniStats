package de.dafeist.unistats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import de.dafeist.unistats.threading.BucketProcessor;
import de.dafeist.unistats.threading.ProcessWorker;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

public class UniStats {
	
	public static File dataFolder;
	public static File decompFolder;
	public static File logFolder;
	public static File targetFolder;
	public static File ddFolder;
	
	public static int logAmt = 0;
	
	public static int logsProcessed = 0;
	public static int linesProcessed = 0;
	
	public static ProgressBar progress;
	
	public static HashMap<Integer, String> playerNames = new HashMap<Integer, String>();
	public static HashMap<String, String> aliases = new HashMap<String, String>();
	
	public static String[] targetServers = {"unicacity.de", "unicacity.de.", "tcpshield.unicacity.de", "tcpshield.unicacity.de.", "mc.unicacity.de", "mc.unicacity.de.", "server.unicacity.de", "server.unicacity.de."};
	
	public static File instanceFolder = new File("C:\\Users\\Feist\\AppData\\Roaming\\.minecraft");
	
	//TODO: Irgendwas stimmt gewaltig mit dem Stats-Counting oder so nicht, viele Daten komplett unrealistisch
	
	//TODO: Estimated PlayTime
	
	public static void main(String[] args) {
		initEnvironment();
		
		prepAliases();
		
		//extractLogs();
		
		processLogs();
		
		
		
		playerNames.put(0, "DaFeist");
		playerNames.put(1, "Feist2007");

		//LogProcessor.process(linesProcessed, logsProcessed);
	}
	
	public static void prepAliases() {
		aliases.put("Feist2007", "DaFeist");
		aliases.put("feist2007", "DaFeist");
	}
	
	public static void processLogs() {
		System.out.println("Vorbereitung läuft...");
		logAmt = decompFolder.listFiles().length;
		System.out.println("Dieser Prozess kann viel CPU-Leistung in Anspruch nehmen");
		
		progress = new ProgressBarBuilder().setTaskName("Bearbeite Datensätze...")
				.setInitialMax(logAmt)
				.setStyle(ProgressBarStyle.ASCII)
				.continuousUpdate()
				.build();
		
		int cores = Runtime.getRuntime().availableProcessors();
		int curr = 0;
		
		Map<Integer, Map<String, List<File>>> splitted = new HashMap<Integer, Map<String, List<File>>>();
		
		Map<String, List<File>> buckets = new BucketProcessor(decompFolder).bucket();
		
		for(int i = 0; i < cores; i++) {
			splitted.put(i, new HashMap<String, List<File>>());
		}
		
		for(Entry<String, List<File>> bucket : buckets.entrySet()) {
			
			if(!(curr < cores)) curr = 0;
				
			splitted.get(curr).put(bucket.getKey(), bucket.getValue());
			curr++;
			
		}
		
		List<Thread> workers = new ArrayList<Thread>();
		
		for(int i = 0; i < cores; i++) {
			Thread worker = new Thread(new ProcessWorker(splitted.get(i)));
			
			worker.start();
			workers.add(worker);
		}
		
		for(Thread worker : workers) {
			try {
				worker.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/*for(int i = 0; i < cores - 1; i++) {
			List<File> queue = new ArrayList<File>();
			queue.add(buckets.values()[curr]);
		}*/
		
		/*int logsProcessed = 0;
		int linesProcessed = 0;
		int appends = 1;
		
		File prev = null;
		File target = null;
		
		for(File file : decompFolder.listFiles()) {
			
			try {
				
			boolean headerLock = false;
			
			FileWriter writer = null;
			
			boolean hasData = false;
			boolean append = false;
			boolean onUC = false;
			boolean prevWasUC = false;
			
			if(prev != null && StringUtils.parseNumDateFromLog(file.getName()).equals(StringUtils.parseNumDateFromLog(prev.getName()).replace(".txt", ""))) {
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
			target = new File(logFolder + "\\" + StringUtils.rmLastChar(file.getName().replace(file.getName().split("-")[3], "")) + ".txt");
			
			try {
				writer = new FileWriter(target);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			}
		
			
			try {
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			    for(String line; (line = reader.readLine()) != null; ) {
			    	
			    	//Check for username
			    	if(line.contains("[main/INFO]: Setting user: ")) {
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
			    				prevWasUC = true;
			    				break;
			    			} else {
			    				onUC = false;
			    			}
			    			
			    		}
			    		if(!onUC && prevWasUC == true) {
			    			writer.write(format(line.split(" ")[0], Action.DISCONNECT, "Disconnected from Server" + " \n" ));
			    			prevWasUC = false;
			    		}
			    	} else if(line.contains("[main/INFO]: Connecting to ")) {
			    		for(String domain : targetServers) {
			    			if(line.split(" ")[4].replace(",", "").equalsIgnoreCase(domain)) {
			    				onUC = true;
			    				if(domain.endsWith(".")) domain =  domain.substring(0, domain.length() - 1);
			    				writer.write(format(line.split(" ")[0], Action.CONNECT, "Connected to Server ") + domain + " \n");
			    				prevWasUC = true;
			    				break;
			    			} else {
			    				onUC = false;
			    			}
			    			
			    		}
			    		if(!onUC && prevWasUC == true) {
			    			writer.write(format(line.split(" ")[0], Action.DISCONNECT, "Disconnected from Server" + " \n" ));
			    			prevWasUC = false;
			    		}
			    	}
			    	
			    	if(!onUC) continue;
			    	
			    	if(line.contains("[CHAT]")) {
			    		hasData = true;
			    		writer.write(format(line.split(" ")[0], Action.CHAT, line.substring(line.indexOf("[CHAT] ") + 7)) + " \n");
			    	}
			        
			    	linesProcessed++;
			    }
			    
			    
		logsProcessed++;
		progress.step();
		
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
		
		} catch (Exception e) {
			e.printStackTrace();
		}
			} catch(IndexOutOfBoundsException e) {
				continue;
			}
			
			}*/
		
	    progress.close();
	    
	    System.out.println("...Erfolg! " + logsProcessed + " Logs gefiltert, " + linesProcessed + " Zeilen verarbeitet");
	    LogProcessor.process(linesProcessed, logsProcessed);
		
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
	   String h11 = "|---------------------------------------------------------------| \n";
		
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
        ddFolder = new File(dataFolder.getPath() + "\\target\\data");
        
		if(!dataFolder.exists()) dataFolder.mkdir();
		if(!decompFolder.exists()) decompFolder.mkdir();
		if(!logFolder.exists()) logFolder.mkdir();
		if(!targetFolder.exists()) targetFolder.mkdir();
		if(!ddFolder.exists()) ddFolder.mkdir();
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
	
	public static void compressGzip(File source, File target) throws IOException {

        try (GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(target));
             FileInputStream fis = new FileInputStream(source)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gos.write(buffer, 0, len);
            }

        }

    }


}
