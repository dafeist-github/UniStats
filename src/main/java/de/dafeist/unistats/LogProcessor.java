package de.dafeist.unistats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import de.dafeist.unistats.UniStats.Action;
import de.dafeist.unistats.stat.RoleplayStatistic;
import de.dafeist.unistats.stat.Statistic;
import de.dafeist.unistats.stat.TimebasedStatistic;
import de.dafeist.unistats.stat.trigger.PredefinedTrigger;
import de.dafeist.unistats.stat.trigger.RoleplayTrigger;
import de.dafeist.unistats.stat.trigger.RoleplayTrigger.METype;
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
			
			for(TimebasedStatistic ts : TimebasedStatistic.statistics) ts.s = null;
			logsProcessed++;
			progress.step();
			
		}
		
		progress.close();
		
	}
	
	public static void initStats() {
		
		Statistic connects = new Statistic("Serverbeitritte", "Du bist UnicaCity x mal beigetreten", Action.CONNECT);
			connects.addTrigger("Connected to Server ");
		Statistic.statistics.add(connects);
		
		Statistic bagopens = new Statistic("Tasche geöffnet", "Du hast x mal deine Tasche geöffnet", Action.OPENBAG);
			for(String player : UniStats.playerNames.values())
			bagopens.addTrigger(player + " öffnet seine Tasche.");
		Statistic.statistics.add(bagopens);
		
		Statistic trashcanopens = new Statistic("Mülleimer durchwühlt", "Du hast x mal einen Mülleimer durchwühlt", Action.OPENTRASH);
			trashcanopens.addTrigger("Du durchwühlst den Mülleimer.");
		Statistic.statistics.add(trashcanopens);
		
		Statistic afks = new Statistic("AFK gegangen", "Du bist x mal AFK gegangen", Action.AFK);
			afks.addTrigger("Du bist nun im AFK-Modus.");
		Statistic.statistics.add(afks);
		
		Statistic adsends = new Statistic("Werbungen geschalten", "Du hast x Werbungen geschalten", Action.SENDAD);
			adsends.addTrigger("[Werbung] Die Werbung wird nun kontrolliert. Bitte gedulde dich ein bisschen, bis die Werbung veröffentlicht wird.");
		Statistic.statistics.add(adsends);
		
		Statistic smssends = new Statistic("SMS's abgesendet", "Du hast x SMS-Nachrichten abgesendet", Action.SENDSMS);
			smssends.addTrigger("SMS abgesendet!");
		Statistic.statistics.add(smssends);
		
		Statistic smsreceives = new Statistic("SMS's erhalten", "Du hast x SMS-Nachrichten erhalten", Action.RECEIVESMS);
			smsreceives.addTrigger("Dein Handy klingelt! Eine Nachricht von ");
			smsreceives.addTrigger("[SMS] Eine SMS von Nummer ");
		Statistic.statistics.add(smsreceives);
		
		Statistic tbonus = new Statistic("Treueboni erhalten", "Du hast x Treueboni erhalten", Action.TBONUS);
			tbonus.addTrigger("[Treuebonus] UnicaCity dankt dir für deine Treue und schenkt dir einen Treuepunkt!");
		Statistic.statistics.add(smsreceives);
		
		Statistic buytequila = new Statistic("Tequila's gekauft", "Du hast x mal einen Tequila gekauft", Action.BUYTEQUILA);
			buytequila.addTrigger("Barkeeper: Hier haben Sie ihren Tequila.");
		Statistic.statistics.add(buytequila);
		
		Statistic buywine = new Statistic("Weine gekauft", "Du hast x mal einen Wein gekauft", Action.BUYWINE);
			buywine.addTrigger("Barkeeper: Hier haben Sie ihren Wein.");
		Statistic.statistics.add(buywine);
		
		Statistic buybeer = new Statistic("Biere gekauft", "Du hast x mal ein Bier gekauft", Action.BUYBEER);
			buybeer.addTrigger("Barkeeper: Hier haben Sie ihr Bier.");
		Statistic.statistics.add(buybeer);
		
		Statistic buyvodka = new Statistic("Vodka's gekauft", "Du hast x mal einen Vodka gekauft", Action.BUYVODKA);
			buyvodka.addTrigger("Barkeeper: Hier haben Sie ihren Vodka.");
		Statistic.statistics.add(buyvodka);
		
		//MSG maybe wrong
		Statistic enabletelephone = new Statistic("Telefon eingeschaltet", "Du hast x mal dein Telefon eingeschaltet", Action.ENABLETELEPHONE);
			enabletelephone.addTrigger("Du hast dein Telefon eingeschalt");
		Statistic.statistics.add(enabletelephone);
		
		Statistic financesshown = new Statistic("Finanzen gezeigt", "Du hast x mal deine Finanzen gezeigt", Action.SHOWFINANCES);
			financesshown.addTrigger(" deine Finanzen gezeigt!");
		Statistic.statistics.add(financesshown);
		
		Statistic persoshows = new Statistic("Personalausweis gezeigt", "Du hast x mal deinen Personalausweis hergezeigt", Action.SHOWPERSO);
			persoshows.addTrigger(" deinen Personalausweis gezeigt!");
		Statistic.statistics.add(persoshows);
		
		Statistic shopbuys = new Statistic("Im Shop eingekauft", "Du hast x Produkte im Shop gekauft", Action.SHOPBUY);
			shopbuys.addTrigger("Verkäufer: Vielen Dank, für Ihren Einkauf. ");
		Statistic.statistics.add(shopbuys);
		
		Statistic screenshots = new Statistic("Screenshots aufgenommen", "Du hast x Screenshots aufgenommen", Action.SCREENSHOT);
			screenshots.addTrigger("Saved screenshot as ");
		Statistic.statistics.add(screenshots);
		
		Statistic callsreceived = new Statistic("Anrufe erhalten", "Du wurdest x mal angerufen", Action.RECEIVECALL);
			callsreceived.addTrigger("Dein Handy klingelt! Ein Anruf von ");
		Statistic.statistics.add(callsreceived);
		
		Statistic callsdenied = new Statistic("Anrufe abgelehnt", "Du hast x Anrufe abgelehnt", Action.DENYCALL);
			callsdenied.addTrigger(" weggedrückt.");
		Statistic.statistics.add(callsdenied);
		
		Statistic revivesseen = new Statistic("Revives gesehen", "Du hast x Revives mitbekommen", Action.SEENREVIVE);
			revivesseen.addTrigger(" wiederbelebt.");
		Statistic.statistics.add(revivesseen);
		
		Statistic pays = new Statistic("Bargeld gezahlt (Spieler)", "Du hast x mal Bargeld an andere Spieler gezahlt", Action.PAYMONEY);
			pays.addPredefinedTrigger("Du hast ", 1, "$ gegeben!");
		Statistic.statistics.add(pays);
		
		Statistic receivemoney = new Statistic("Bargeld bekommen (Spieler)", "Du hast x mal Bargeld von anderen Spielern bekommen", Action.RECEIVEMONEY);
			receivemoney.addPredefinedTrigger(" hat dir ", 1, "$ gegeben!");
		Statistic.statistics.add(receivemoney);
		
		Statistic transferbankmoney = new Statistic("Geld überwiesen", "Du hast x mal Geld an einen anderen Spieler überwiesen", Action.TRANSFERBANKMONEY);
			transferbankmoney.addPredefinedTrigger(null, 0, null);
			//Ich brauch die Message noch /:
		Statistic.statistics.add(transferbankmoney);
		
		Statistic receivebankmoney = new Statistic("Geld durch Überweisungen bekommen", "Du hast x mal Geld durch Überweisungen bekommen", Action.RECEIVEBANKMONEY);
			receivebankmoney.addPredefinedTrigger(" hat dir ", 1, "$ überwiesen!");
		Statistic.statistics.add(receivebankmoney);
			
		Statistic gunloads = new Statistic("Waffe geladen", "Du hast x mal deine Waffe geladen", Action.LOADGUN);
			gunloads.addPredefinedTrigger(" mit ", 1, " Kugeln beladen.");
		Statistic.statistics.add(gunloads);
		
		Statistic withdraws = new Statistic("Bankabhebungen", "Du hast x mal Geld von der Bank abgehoben", Action.WITHDRAW);
			withdraws.addTrigger("Auszahlung: -");
		Statistic.statistics.add(withdraws);
		
		Statistic deposits = new Statistic("Bankeinzahlungen", "Du hast x mal Geld in die Bank eingezahlt", Action.DEPOSIT);
			deposits.addTrigger("Eingezahlt: +");
		Statistic.statistics.add(deposits);
		
		Statistic paydays = new Statistic("PayDays", "Du hast x PayDays bekommen", Action.PAYDAY);
			paydays.addTrigger("======== PayDay ========");
		Statistic.statistics.add(paydays);
		
		//Kills are kind of unreliable, because they only get shown as Karma-Points in Logs
		Statistic kills = new Statistic("Kills", "Du hast x Spieler gekillt", Action.KILL);
			kills.addTrigger("[Karma] -2 Karma.");
			kills.addTrigger("[Karma] -3 Karma.");
			kills.addTrigger("[Karma] -4 Karma.");
			kills.addTrigger("[Karma] -5 Karma.");
		Statistic.statistics.add(kills);
		
		//Timebased
		TimebasedStatistic jailtime = new TimebasedStatistic("Im Gefängnis gelandet", "Du warst x mal im Gefängnis", Action.JAILED, Action.UNJAILED);
			jailtime.addPredefinedTrigger("Du bist nun für ", 60, "Minuten im Gefängnis.");
		TimebasedStatistic.statistics.add(jailtime);
		
		TimebasedStatistic calltime = new TimebasedStatistic("Anrufszeit", "Du warst x mal in einem Anruf", Action.STARTCALL, Action.ENDCALL);
			calltime.addStartTrigger(" hat den Anruf angenommen.");
			calltime.addStartTrigger("Du hast den Anruf von ");
			calltime.addStartTriggerBlacklist("abgelehnt");
			calltime.addEndTrigger("Der Gesprächspartner hat den Anruf beendet.");
			calltime.addEndTrigger("Du hast den Anruf weggedrückt.");
		TimebasedStatistic.statistics.add(calltime);
		
		//Roleplay Actions
		RoleplayStatistic totalSelf = new RoleplayStatistic("Gesamt /me's ausgeführt", "Du hast x mal /me ausgeführt");
			RoleplayStatistic.hardcoded.add(totalSelf);
		RoleplayStatistic totalOther = new RoleplayStatistic("Gesamt /me's an dir ausgeführt", "Andere Spieler haben x mal eine /me Aktion an dir ausgeführt");
			RoleplayStatistic.hardcoded.add(totalOther);
		RoleplayStatistic total = new RoleplayStatistic("Gesamt /me's gesehen", "Du hast insgesamt x mal ein /me gesehen, egal von wem");
			RoleplayStatistic.hardcoded.add(total);
		
		//TODO: Wie viel Alkohol insgesamt gekauft?
		
		//TODO: Überweisungen
		//TODO: Alles mit /me's und Chats
			
		//WICHTIG: BEI SERVER-LEAVE UNBEDINGT TIME-TRIGGERS RESETTEN!!!
		
		//Vielleicht machen Umlaute Probleme?
	}
	
	public static void analyzeLine(Line line) {
		
		//Failsafe
		if(line.getContent().contains("[UniStats] Detected new Instance-Start,")) for(TimebasedStatistic ts : TimebasedStatistic.statistics) ts.s = null;
		
		//Normal Stats
		for(Statistic statistic : Statistic.statistics) {
			for(String string : statistic.triggers) {
				if(line.getContent().contains(string)) {
					statistic.count();
					if(statistic.actionTrigger != null) line.setAction(statistic.actionTrigger);
				}
			}
			for(PredefinedTrigger trigger : statistic.predefinedTriggers) {
				if(line.getContent().contains(trigger.before) && line.getContent().contains(trigger.after)) {
					String between = line.getContent().substring(line.getContent().indexOf(trigger.before) + 1, line.getContent().indexOf(trigger.after));
					int amt = Integer.parseInt(between) * trigger.multiplier;
					statistic.addValue(amt);
				}
			}
			
		}
		
		//Timebased Stats
		for(TimebasedStatistic statistic : TimebasedStatistic.statistics) {
			for(String string : statistic.startTriggers) {
				if(line.getContent().contains(string)) {
					boolean l = false;
					for(String blacklist : statistic.startTriggerBlacklist) {
						if(string.contains(blacklist)) l = true;
					}
					if(l) continue;
					statistic.s = line.getTime();
					line.setAction(statistic.startActionTrigger);
				}
			}
			
			for(String string : statistic.endTriggers) {
				if(line.getContent().contains(string)) {
					if(statistic.s == null) continue;
					statistic.add(Line.timeDiff(statistic.s, line.getTime()));
					line.setAction(statistic.endActionTrigger);
					statistic.s = null;
				}
			}
			
			for(PredefinedTrigger trigger : statistic.predefinedTriggers) {
				if(line.getContent().contains(trigger.before) && line.getContent().contains(trigger.after)) {
					String between = line.getContent().substring(line.getContent().indexOf(trigger.before) + 1, line.getContent().indexOf(trigger.after));
					int amt = Integer.parseInt(between) * trigger.multiplier;
					statistic.add(amt);
				}
			}
		}
		
		//RolePlay Stats
		for(RoleplayStatistic statistic : RoleplayStatistic.statistics) {
			for(RoleplayTrigger trigger : statistic.triggers) {
				String content = line.getContent();
				boolean ret = false;
				
				for(String s : trigger.include) {
					if(!content.contains(s)) ret = true;
				}
				
				for(String s : trigger.exclude) {
					if(content.contains(s)) ret = true;
				}
				
				if(!ret) {
					for(String name : UniStats.playerNames.values()) {
						
						if(content.startsWith("* ")) RoleplayStatistic.hardcoded.get(2).count();
						
						if(content.startsWith("* " + name) && trigger.type == METype.SELF) {
							//SELF
							RoleplayStatistic.hardcoded.get(0).count();
							statistic.count();
						} else if(content.startsWith("* ") && content.contains(name) && trigger.type == METype.OTHER) {
							//OTHER
							RoleplayStatistic.hardcoded.get(1).count();
							statistic.count();
						} else if(content.startsWith("* ") && trigger.type == METype.ANY) {
							//ANY
							statistic.count();
						}
					}
				}
				
			}
		}
		
		
	}
	
}
