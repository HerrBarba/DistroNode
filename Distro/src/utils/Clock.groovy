package utils

@Singleton
class Clock {
	long origin // millis
	long offset // nanos
	
	void startClock(String initialTime) {
		origin = DateUtils.formatString2Date(initialTime).getTime()
		offset = System.nanoTime()
	}

	void add(long/*nanos*/time) {
		origin += toMillis(time)
		offset = System.nanoTime()
	}
		
	String getTime() {
		long elapsedTime = System.nanoTime() - offset
		long currentTime = origin + toMillis(elapsedTime)
		return DateUtils.formatDate2String(new Date(currentTime))
	}
	
	long toMillis(long nanos) {
		return nanos / 1000000
	}
}
