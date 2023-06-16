package de.dafeist.unistats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import de.dafeist.unistats.UniStats.Action;
import de.dafeist.unistats.stat.CalculatedStatistic;
import de.dafeist.unistats.stat.ConvoStatistic;
import de.dafeist.unistats.stat.RoleplayStatistic;
import de.dafeist.unistats.stat.RoleplayStatistic.RPAction;
import de.dafeist.unistats.stat.Statistic;
import de.dafeist.unistats.stat.TimebasedStatistic;
import de.dafeist.unistats.stat.trigger.ConvoTrigger;
import de.dafeist.unistats.stat.trigger.ConvoTrigger.MSender;
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
			
				for(TimebasedStatistic ts : TimebasedStatistic.statistics) ts.s = null;
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				FileWriter writer = new FileWriter(targetFolder.getPath() + "\\data\\" + file.getName());
				
				int c = 0;
				
				//To process all the data, we gotta insert all data into an Array
				for(String line; (line = reader.readLine()) != null; ) {
					//System.out.println(line);
					
					//Failsafe
					if(line.contains("[UniStats] Detected new Instance-Start,")) for(TimebasedStatistic ts : TimebasedStatistic.statistics) ts.s = null;
					
					if(c > 12 && line != null && !line.isEmpty() && line.startsWith("[") && !line.contains("[UniStats]")) lines.add(Line.fromString(line));
					c++;
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
			
			//Flush Data
			for(TimebasedStatistic ts : TimebasedStatistic.statistics) ts.s = null;
			lines.clear();
			
			logsProcessed++;
			
			progress.step();
			
		}
		
		for(Statistic statistic : Statistic.statistics) {
			System.out.println(statistic.name + " | Count: " + statistic.count);
		}
		
		for(TimebasedStatistic statistic : TimebasedStatistic.statistics) {
			System.out.println(statistic.name + " | Time: " + statistic.time / 60);
		}
		
		for(RoleplayStatistic statistic : RoleplayStatistic.statistics) {
			System.out.println(statistic.name + " | Count: " + statistic.count);
		}
		
		for(RoleplayStatistic statistic : RoleplayStatistic.hardcoded) {
			System.out.println(statistic.name + " | Count: " + statistic.count);
		}
		
		for(CalculatedStatistic statistic : CalculatedStatistic.statistics) {
			statistic.calc();
			System.out.println(statistic.name + " | Count: " + statistic.count);
		}
		
		for(ConvoStatistic statistic : ConvoStatistic.statistics) {
			System.out.println(statistic.name + " | Count: " + statistic.count);
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
		
		Statistic deaths = new Statistic("Tode", "Du bist x mal gestorben", Action.DEATH);
			deaths.addTrigger("kannst du sehen, wie lange du noch tot bist. ");
		Statistic.statistics.add(deaths);
		
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
		Statistic.statistics.add(tbonus);
		
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
			pays.addPredefinedTrigger("Du hast ", 1, "$ gegeben!", true);
		Statistic.statistics.add(pays);
		
		Statistic receivemoney = new Statistic("Bargeld bekommen (Spieler)", "Du hast x mal Bargeld von anderen Spielern bekommen", Action.RECEIVEMONEY);
			receivemoney.addPredefinedTrigger(" hat dir ", 1, "$ gegeben!");
		Statistic.statistics.add(receivemoney);
		
		/*Statistic transferbankmoney = new Statistic("Geld überwiesen", "Du hast x mal Geld an einen anderen Spieler überwiesen", Action.TRANSFERBANKMONEY);
			transferbankmoney.addPredefinedTrigger(null, 0, null);
			//Ich brauch die Message noch /:
		Statistic.statistics.add(transferbankmoney);*/
		
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
		
		TimebasedStatistic afks = new TimebasedStatistic("AFK-Zeit", "Du bist x mal AFK gegangen", Action.AFK, Action.NOAFK);
			afks.addStartTrigger("Du bist nun im AFK-Modus.");
			afks.addEndTrigger("Du bist nun nicht mehr im AFK-Modus.");
		TimebasedStatistic.statistics.add(afks);
		
		TimebasedStatistic calltime = new TimebasedStatistic("Anrufszeit", "Du warst x mal in einem Anruf", Action.STARTCALL, Action.ENDCALL);
			calltime.addStartTrigger(" hat den Anruf angenommen.");
			calltime.addStartTrigger("Du hast den Anruf von ");
			calltime.addStartTriggerBlacklist("abgelehnt");
			calltime.addEndTrigger("Der Gesprächspartner hat den Anruf beendet.");
			calltime.addEndTrigger("Du hast den Anruf weggedrückt.");
			calltime.addEndTrigger("Du hast aufgelegt.");
		TimebasedStatistic.statistics.add(calltime);
		
		//Roleplay Actions
		RoleplayStatistic totalSelf = new RoleplayStatistic("Gesamt /me's ausgeführt", "Du hast x mal /me ausgeführt");
			RoleplayStatistic.hardcoded.add(totalSelf);
			
		RoleplayStatistic totalOther = new RoleplayStatistic("Gesamt /me's an dir ausgeführt", "Andere Spieler haben x mal eine /me Aktion an dir ausgeführt");
			RoleplayStatistic.hardcoded.add(totalOther);
			
		RoleplayStatistic total = new RoleplayStatistic("Gesamt /me's gesehen", "Du hast insgesamt x mal ein /me gesehen, egal von wem");
			RoleplayStatistic.hardcoded.add(total);
			
		RoleplayStatistic packenTotal = new RoleplayStatistic("/me packt xy gesehen", "Du hast insgesamt x mal gesehen, wie jemand gepackt wurde", RPAction.GEPACKTANY);
			packenTotal.addTrigger(new RoleplayTrigger(METype.ANY, " packt "));
		RoleplayStatistic.statistics.add(packenTotal);
		
		RoleplayStatistic packen = new RoleplayStatistic("Leute gepackt", "Du hast x mal Jemanden gepackt", RPAction.PACKEN);
			packen.addTrigger(new RoleplayTrigger(METype.SELF, " packt "));
		RoleplayStatistic.statistics.add(packen);
		
		RoleplayStatistic gepackt = new RoleplayStatistic("Gepackt worden", "Du wurdest x mal gepackt", RPAction.GEPACKT);
			gepackt.addTrigger(new RoleplayTrigger(METype.OTHER, " packt "));
		RoleplayStatistic.statistics.add(gepackt);
		
		RoleplayStatistic knebeln = new RoleplayStatistic("Leute geknebelt", "Du hast x mal eine Person geknebelt", RPAction.KNEBELN);
			knebeln.addTrigger(new RoleplayTrigger(METype.SELF, " knebelt "));
		RoleplayStatistic.statistics.add(knebeln);
		
		RoleplayStatistic geknebelt = new RoleplayStatistic("Geknebelt worden", "Du wurdest x mal geknebelt", RPAction.GEKNEBELT);
			geknebelt.addTrigger(new RoleplayTrigger(METype.OTHER, " knebelt "));
		RoleplayStatistic.statistics.add(geknebelt);
		
		RoleplayStatistic fesseln = new RoleplayStatistic("Leute gefesselt", "Du hast x mal eine Person gefesselt", RPAction.FESSELN);
			fesseln.addTrigger(new RoleplayTrigger(METype.SELF, " fesselt "));
		RoleplayStatistic.statistics.add(fesseln);
		
		RoleplayStatistic gefesselt = new RoleplayStatistic("Gefesselt worden", "Du wurdest x mal gefesselt", RPAction.GEFESSELT);
			gefesselt.addTrigger(new RoleplayTrigger(METype.OTHER, " fesselt "));
		RoleplayStatistic.statistics.add(gefesselt);
		
		RoleplayStatistic laugh = new RoleplayStatistic("Gelacht", "Du hast x mal gelacht", RPAction.LACHEN);
			laugh.addTrigger(new RoleplayTrigger(METype.SELF, " lacht"));
		RoleplayStatistic.statistics.add(laugh);
		
		RoleplayStatistic laughany = new RoleplayStatistic("Lachen gesehen", "Du hast x mal jemanden lachen gesehen", RPAction.LACHENANY);
			laughany.addTrigger(new RoleplayTrigger(METype.ANY, " lacht"));
		RoleplayStatistic.statistics.add(laughany);
		
		RoleplayStatistic laughE = new RoleplayStatistic("Gelächelt", "Du hast x mal gelächelt", RPAction.LAECHELN);
			laughE.addTrigger(new RoleplayTrigger(METype.SELF, " lächelt"));
		RoleplayStatistic.statistics.add(laughE);
	
		RoleplayStatistic laughanyE = new RoleplayStatistic("Lächeln gesehen", "Du hast x mal jemanden lächeln gesehen", RPAction.LAECHELNANY);
			laughanyE.addTrigger(new RoleplayTrigger(METype.ANY, " lächelt"));
		RoleplayStatistic.statistics.add(laughanyE);
		
		RoleplayStatistic schmunzeln = new RoleplayStatistic("Gechmunzelt", "Du hast x mal geschmunzelt", RPAction.SCHMUNZELN);
			schmunzeln.addTrigger(new RoleplayTrigger(METype.SELF, " schmunzelt"));
		RoleplayStatistic.statistics.add(schmunzeln);
	
		RoleplayStatistic schmunzelnany = new RoleplayStatistic("Schmunzeln gesehen", "Du hast x mal jemanden schmunzeln gesehen", RPAction.SCHMUNZELNANY);
			schmunzelnany.addTrigger(new RoleplayTrigger(METype.ANY, " schmunzelt"));
		RoleplayStatistic.statistics.add(schmunzelnany);
		
		RoleplayStatistic kiss = new RoleplayStatistic("Geküsst", "Du hast x mal jemanden geküsst", RPAction.KISS);
			kiss.addTrigger(new RoleplayTrigger(METype.SELF, new String[]{" gibt ", " einen Kuss."}));
		RoleplayStatistic.statistics.add(kiss);
		
		RoleplayStatistic kissother = new RoleplayStatistic("Geküsst worden", "Du wurdest x mal geküsst", RPAction.KISSOTHER);
			kissother.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" gibt ", " einen Kuss."}));
		RoleplayStatistic.statistics.add(kissother);

		RoleplayStatistic kissany = new RoleplayStatistic("Küsse gesehen", "Du hast x mal jemanden beim Küssen gesehen", RPAction.KISSANY);
			kissany.addTrigger(new RoleplayTrigger(METype.ANY, new String[]{" gibt ", " einen Kuss."}));
		RoleplayStatistic.statistics.add(kissany);
		
		RoleplayStatistic nicken = new RoleplayStatistic("Genickt", "Du hast x mal genickt", RPAction.NICKEN);
		nicken.addTrigger(new RoleplayTrigger(METype.SELF, " nickt"));
		RoleplayStatistic.statistics.add(nicken);

		RoleplayStatistic nickenany = new RoleplayStatistic("Nicken gesehen", "Du hast x mal jemanden nicken gesehen", RPAction.NICKENANY);
		nickenany.addTrigger(new RoleplayTrigger(METype.ANY, " nickt"));
		RoleplayStatistic.statistics.add(nickenany);
		
		RoleplayStatistic ansehen = new RoleplayStatistic("Personen angesehen", "Du hast x mal jemanden angesehen", RPAction.ANSEHEN);
			ansehen.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" sieht ", " an "}));
			ansehen.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" schaut ", " an "}));
			ansehen.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" sieht ", " zu "}));
			ansehen.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" schaut ", " zu "}));
			ansehen.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" sieht ", " an "}));
		RoleplayStatistic.statistics.add(ansehen);
		
		RoleplayStatistic ansehenother = new RoleplayStatistic("Angesehen worden", "Du wurdest x mal angesehen", RPAction.ANSEHENOTHER);
			ansehenother.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" sieht ", " an "}));
			ansehenother.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" schaut ", " an "}));
			ansehenother.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" sieht ", " zu "}));
			ansehenother.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" schaut ", " zu "}));
			ansehenother.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" sieht ", " an "}));
		RoleplayStatistic.statistics.add(ansehenother);

		RoleplayStatistic ansehenany = new RoleplayStatistic("Ansehen gesehen", "Du hast x mal jemanden jemanden ansehen gesehen", RPAction.ANSEHENANY);
			ansehenany.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" sieht ", " an "}));
			ansehenany.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" schaut ", " an "}));
			ansehenany.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" sieht ", " zu "}));
			ansehenany.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" schaut ", " zu "}));
			ansehenany.addTrigger(new RoleplayTrigger(METype.OTHER, new String[]{" sieht ", " an "}));
		RoleplayStatistic.statistics.add(ansehenany);
		
		RoleplayStatistic grin = new RoleplayStatistic("Gegrinst", "Du hast x mal gegrinst", RPAction.GRINSEN);
		grin.addTrigger(new RoleplayTrigger(METype.SELF, " grinst"));
		RoleplayStatistic.statistics.add(grin);

		RoleplayStatistic grinany = new RoleplayStatistic("Grinsen gesehen", "Du hast x mal jemanden grinsen gesehen", RPAction.GRINSENANY);
			grinany.addTrigger(new RoleplayTrigger(METype.ANY, " grinst"));
		RoleplayStatistic.statistics.add(grinany);
		
		RoleplayStatistic tragen = new RoleplayStatistic("Personen getragen", "Du hast x mal jemanden getragen", RPAction.TRAGEN);
			tragen.addTrigger(new RoleplayTrigger(METype.SELF, " trägt"));
		RoleplayStatistic.statistics.add(tragen);
		
		RoleplayStatistic tragenother = new RoleplayStatistic("Getragen worden", "Du wurdest x mal getragen", RPAction.TRAGENOTHER);
			tragenother.addTrigger(new RoleplayTrigger(METype.OTHER, " trägt"));
		RoleplayStatistic.statistics.add(tragenother);
	
		RoleplayStatistic tragenany = new RoleplayStatistic("Tragen gesehen", "Du hast x mal jemanden jemanden tragen gesehen", RPAction.TRAGENANY);
			tragenany.addTrigger(new RoleplayTrigger(METype.ANY, " trägt"));
		RoleplayStatistic.statistics.add(tragenany);
		
		RoleplayStatistic schubsen = new RoleplayStatistic("Personen geschubst", "Du hast x mal jemanden geschubst", RPAction.SCHUBSEN);
			schubsen.addTrigger(new RoleplayTrigger(METype.SELF, " schubst"));
		RoleplayStatistic.statistics.add(schubsen);
		
		RoleplayStatistic schubsenother = new RoleplayStatistic("Geschubst worden", "Du wurdest x mal geschubst", RPAction.SCHUBSENOTHER);
			schubsenother.addTrigger(new RoleplayTrigger(METype.OTHER, " schubst"));
		RoleplayStatistic.statistics.add(schubsenother);
		
		RoleplayStatistic schubsenany = new RoleplayStatistic("Schubsen gesehen", "Du hast x mal jemanden jemanden schubsen gesehen", RPAction.SCHUBSENANY);
			schubsenany.addTrigger(new RoleplayTrigger(METype.ANY, " schubst"));
		RoleplayStatistic.statistics.add(schubsenany);
		
		//Calculated
		
		CalculatedStatistic alcbuy = new CalculatedStatistic("Alkohol gekauft", "Du hast x mal Alkohol gekauft");
			alcbuy.add(buytequila);
			alcbuy.add(buywine);
			alcbuy.add(buybeer);
			alcbuy.add(buyvodka);
		CalculatedStatistic.statistics.add(alcbuy);
		
		//Convo
		
		//TODO: Überweisungen
		
		//TODO: Alles mit Chats und so
		
		//Vielleicht machen Umlaute Probleme?
	}
	
	public static void analyzeLine(Line line) {
		
		//Normal Stats
		for(Statistic statistic : Statistic.statistics) {
			for(String string : statistic.triggers) {
				if(line.getContent().contains(string)) {
					statistic.count();
					if(statistic.actionTrigger != null) line.setAction(statistic.actionTrigger);
				}
			}
			
			if(!statistic.predefinedTriggers.isEmpty()) {
			for(PredefinedTrigger trigger : statistic.predefinedTriggers) {
				if(line.getContent().contains(trigger.before) && line.getContent().contains(trigger.after)) {
					String between = line.getContent().substring(line.getContent().indexOf(trigger.before) + trigger.before.length() + 1, line.getContent().indexOf(trigger.after));
					between = between.replaceAll("[^0-9]", "");
					if(!between.isBlank()) {
					int amt = Integer.parseInt(between) * trigger.multiplier;
					statistic.addValue(amt);
					statistic.count();
					}
				}
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
					String between = line.getContent().substring(line.getContent().indexOf(trigger.before) + trigger.before.length() + 1, line.getContent().indexOf(trigger.after));
					int amt = Integer.parseInt(between.replace(" ", "")) * trigger.multiplier;
					statistic.add(amt);
				}
			}
		}
		
		if(line.getContent().startsWith("* ")) RoleplayStatistic.hardcoded.get(2).count();
		
		//RolePlay Stats
		for(RoleplayStatistic statistic : RoleplayStatistic.statistics) {
			for(RoleplayTrigger trigger : statistic.triggers) {
				String content = line.getContent();
				boolean fine = false;
				
				if(trigger.include != null && trigger.include.length >= 1) {
				for(String s : trigger.include) {
					if(content.contains(s)) fine = true;
				}
				
				if(trigger.exclude != null && trigger.exclude.length >= 1) {
				for(String s : trigger.exclude) {
					if(content.contains(s)) fine = false;
				}
					}
						}
				
				if(fine) {
					if(content.startsWith("* ") && trigger.type == METype.ANY) {
							//ANY
							statistic.count();
					}
					
					for(String name : UniStats.playerNames.values()) {
						
						if(content.startsWith("* " + name) && trigger.type == METype.SELF) {
							//SELF
							RoleplayStatistic.hardcoded.get(0).count();
							statistic.count();
						} else if(content.startsWith("* ") && content.contains(name) && !content.startsWith("* " + name) && trigger.type == METype.OTHER) {
							//OTHER
							RoleplayStatistic.hardcoded.get(1).count();
							statistic.count();
						}
					}
				}
				
			}
		}
		
		//Convo Stats
		for(ConvoStatistic statistic : ConvoStatistic.statistics) {
			for(ConvoTrigger trigger : statistic.triggers) {
				String content = line.getContent();
				boolean fine = false;
				
				if(trigger.include != null && trigger.include.length >= 1) {
				for(String s : trigger.include) {
					if(content.contains(s)) fine = true;
				}
				
				if(trigger.exclude != null && trigger.exclude.length >= 1) {
				for(String s : trigger.exclude) {
					if(content.contains(s)) fine = false;
				}
					}
						}
				
				if(fine) {
					if(content.startsWith("[") && trigger.msender == MSender.ANY) {
						//ANY
						statistic.count();
					}
					
					for(String name : UniStats.playerNames.values()) {
						if(content.startsWith("[") && content.contains("] " + name + " ")) {
							//SELF
							statistic.count();
						}
						
					}
				}
				
			}
		}
		
	}
	
}
