package de.dafeist.unistats.parse;

public class StringUtils {
	
	public static String parseNumDateFromLog(String string) {
		String[] split = string.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		
		return day + "-" + month + "-" + year;
	}
	
	public static String formatDate(String string) {
		String split[] = string.split("-");
		String nDay = split[0];
		String nMonth = split[1];
		String nYear = split[2];
		
		String month;
		
		if(nMonth.startsWith("0")) nMonth.replace("0", "");
		
		switch(Integer.parseInt(nMonth)) {
		  case 1:
			  month = "Januar";
		    break;
		  case 2:
			  month = "Februar";   
		    break;
		  case 3:
			  month = "März";
			break;
		  case 4:
			  month = "April";
			break;
		  case 5:
			  month = "Mai";
			break;
		  case 6:
			  month = "Juni";
			break;
		  case 7:
			  month = "Juli";
			break;
		  case 8:
			  month = "August";
			break;
		  case 9: 
			  month = "September";
			break;
		  case 10:
			  month = "Oktober";
			break;
		  case 11:
			  month = "November";
			break;
		  case 12:
			  month = "Dezember";
			break;
		  default:
			  month = "";
			break;
		}
		
		if(nDay.startsWith("0")) nDay.replace("0", "");
		return nDay + ". " + month + " " + nYear;
	}
	
}
