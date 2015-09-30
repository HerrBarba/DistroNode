package utils

@Singleton
class Clock {
	long origin
	long offset
	
	void startClock(String initialTime) {
		origin = DateUtils.formatString2Date(initialTime).getTime()
		offset = System.nanoTime()
	}

	void add(long time) {
		origin += toMillis(time)
		offset += time
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
