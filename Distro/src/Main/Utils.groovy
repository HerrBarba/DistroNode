package Main

import java.text.SimpleDateFormat;

class Utils {	
	static SimpleDateFormat format
	
	static {
		format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
		format.setTimeZone(TimeZone.getTimeZone("UTC"))
	}
	
	static String formatDate2String(Date date) {		
		return format.format(date)
	}
	
	static Date formatString2Date(String date) {
		return format.parse(date)
	}
}
