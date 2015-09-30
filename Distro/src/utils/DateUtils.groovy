package utils

import java.text.SimpleDateFormat;

class DateUtils {	
	static SimpleDateFormat format
	
	static {
		format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss,SSSX")
		format.setTimeZone(TimeZone.getTimeZone("UTC"))
	}
	
	static String formatDate2String(Date date) {		
		return format.format(date)
	}
	
	static Date formatString2Date(String date) {
		return format.parse(date)
	}
}
